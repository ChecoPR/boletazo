package com.itq.progradist.boletazo.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.itextpdf.text.DocumentException;
import com.itq.progradist.boletazo.ftp.documento.BoletazoDocument;
import com.itq.progradist.boletazo.ftp.exceptions.BoletazoFtpClientException;
import com.itq.progradist.boletazo.ftp.modelos.Evento;
import com.itq.progradist.boletazo.ftp.modelos.Lugar;
import com.itq.progradist.boletazo.ftp.params.ParamNames;
import com.itq.progradist.boletazo.ftp.params.Response;

/**
 * Clase que contiene la funcionalidad para subir los informes al servidor FTP
 * 
 * @author Equipo 5
 *
 */
public class InformesTimerTask extends TimerTask {
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(InformesTimerTask.class);
	
	/*
	 * Dirección IP del servidor boletazo
	 */
	static final String BOLETAZO_ADDRESS = "localhost";
	
	/*
	 * Puerto del servidor boletazo
	 */
	static final int PORT_ADDRESS = 5000;
	
	// private static final int DELAY = 10000;
	
	/**
	 * El timer task se ejecutará despues del tiempo que indique
	 */
	private static final int DELAY = 86400000; // un día

	/**
	 * Obtiene datos del servidor boletazo, genera un informe y sube un respaldo 
	 * al servidor FTP
	 */
	@Override
	public void run() {
		Object[] data = getData();
		@SuppressWarnings("unchecked")
		List<Lugar> lugares = (List<Lugar>) data[1];
		
		String today = (String) data[0];
		
		try {
			
			logger.info("Generando informes de ventas del día " + today);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			DateFormat monthAndYear = new SimpleDateFormat("yyyy-MM");
			Date date = new Date();
			
			String pdfName;
			String dirName;
			String lugarName;
			BoletazoDocument boletazoDocument;
			
			for (Lugar lugar : lugares) {
				logger.info("Generando informe de ventas de al sede " + lugar.getIdLugar() + " del día " + today);
				
				lugarName = lugar.getNombre().replaceAll(" ", "-");
				pdfName = dateFormat.format(date) + "-" + lugarName;
				dirName = lugarName + "/" + monthAndYear.format(date) + "/";
				
				boletazoDocument = new BoletazoDocument(dirName, pdfName);
				
				boletazoDocument.open();
				
				boletazoDocument.addBoletazoHeader(today, lugar.getNombre());
				
				List<Evento> eventos = lugar.getEventos();
				boletazoDocument.addEventos(eventos);
				
				double totalVentas = 0;
				for (Evento evento: eventos) {
					totalVentas += evento.getTotal();
				}
				boletazoDocument.addBoletazoFooter(totalVentas);
				
				boletazoDocument.close();
				
				logger.info("Informe de ventas de la sede " + lugar.getIdLugar() + " generado exitosamente, día:" + today);
				
				logger.info("Comenzando la subida del respaldo del archivo " + boletazoDocument.getFullName());
				
				BoletazoFtpClient ftpClient = new BoletazoFtpClient();
				try {
					ftpClient.connectToBoletazoFtpServer();
					ftpClient.loginToBoletazoFtpServer();
					ftpClient.uploadBoletazoDocument(boletazoDocument);
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException e) {
					logger.error("Error al subir el documento " + boletazoDocument.getPdfName() + ": " + e.getMessage());
					logger.catching(e);
				} catch (BoletazoFtpClientException e) {
					logger.error("Error al subir el documento " + boletazoDocument.getPdfName() + ": " + e.getMessage());
					logger.catching(e);
				}
			}
			
		} catch (DocumentException e) {
			logger.error("Error al construir el documento: " + e.getMessage());
			logger.catching(e);
		} catch (FileNotFoundException e) {
			logger.error("Archivo no encontrado: " + e.getMessage());
			logger.catching(e);
		}
	}

	/**
	 * Agenda el proceso según el tiempo configurado
	 */
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(this, 0, DELAY);
	}
	
	/**
	 * Hace una petición al servidor boletazo para obtener la información 
	 * que contendrá el informe.
	 * 
	 * @return data Contiene la fecha del informe y la información
	 */
	private Object[] getData() {
		JSONObject dataRequest = new JSONObject();
		
		dataRequest.put(ParamNames.Recurso.KEY_NAME, ParamNames.Recurso.LugarVentas.VALUE);
		dataRequest.put(ParamNames.Metodo.KEY_NAME, ParamNames.Metodo.Values.GET);
		
		logger.info("Inicia la ejecución del cliente.");
		
		Socket clientSocket;
		String input = "";
		try {
			clientSocket = new Socket(BOLETAZO_ADDRESS, PORT_ADDRESS);
			
			OutputStream outputStream = clientSocket.getOutputStream();
			
			DataOutputStream flowOut = new DataOutputStream(outputStream);
			
			flowOut.writeUTF(dataRequest.toString());
			
			InputStream inputStream = clientSocket.getInputStream();
			
			DataInputStream dataIn = new DataInputStream(inputStream);
			
			input = dataIn.readUTF().toString();
			
			logger.info("Respuesta del server: [" + input + "]");
			
			clientSocket.close();
		} catch (UnknownHostException e) {
			logger.error("Error solicitando información de ventas: " + e.getMessage());
			logger.catching(e);
		} catch (IOException e) {
			logger.error("Error solicitando información de ventas: " + e.getMessage());
			logger.catching(e);
		}
		
		JSONObject responseJson = new JSONObject(input.replaceAll("\\\"", "\""));
		String day = responseJson.getString(Response.Lugar.Informe.DAY);
		List<Lugar> lugares = decodeListArray(responseJson);
		Object[] data = {day, lugares};
		
		return data;
	}
	
	/**
	 * Convierte el JSON a una lista de lugares
	 * 
	 * @param responseJson JSON que contiene los lugares
	 * @return lugares Lugares en formato de lista
	 */
	private List<Lugar> decodeListArray(JSONObject responseJson) {
		JSONArray lugaresJsonArray = responseJson.getJSONArray("data");
		List<Lugar> lugares = new ArrayList<>();
		for (int i = 0; i < lugaresJsonArray.length(); i++) {
			JSONObject apartadoJson = lugaresJsonArray.getJSONObject(i);
			lugares.add(getApartadoFromJsonObject(apartadoJson));
		}
		return lugares;
	}
	
	/**
	 * Convierte un JSON que representa el lugar a un 
	 * objeto de tipo Lugar
	 * 
	 * @param lugarJson JSON de lugar
	 * @return lugar Lista con la información del lugar
	 */
	private Lugar getApartadoFromJsonObject(JSONObject lugarJson) {
		int idLugar = lugarJson.getInt(Response.Lugar.Values.ID_LUGAR);
		String nombre = lugarJson.getString(Response.Lugar.Values.NOMBRE);
		String estado = lugarJson.getString(Response.Lugar.Values.ESTADO);
		List<Evento> eventos = decodeEventosJsonArray(
				lugarJson.getJSONArray(Response.Lugar.Values.EVENTOS)
			);
		Lugar lugar = new Lugar(idLugar, nombre, estado);
		lugar.setEventos(eventos);
		return lugar;
	}
	
	/**
	 * Convierte el JSON a una lista de eventoos
	 * 
	 * @param eventosArray
	 * @return eventos
	 */
	private List<Evento> decodeEventosJsonArray(JSONArray eventosArray) {
		List<Evento> eventos = new ArrayList<Evento>();
		for (int i = 0; i < eventosArray.length(); i++) {
			eventos.add(
					getEventoFromJsonObject(eventosArray.getJSONObject(i))
				);
		}
		return eventos;
	}
	
	/**
	 * Convierte un JSON que representa el evento a un 
	 * objeto de tipo Evento
	 * 
	 * @param eventoJson Evento en tipo JSON
	 * @return evento
	 */
	private Evento getEventoFromJsonObject(JSONObject eventoJson) {
		int idEvento = eventoJson.getInt(Response.Lugar.Evento.ID_EVENTO);
		int idLugar = eventoJson.getInt(Response.Lugar.Evento.ID_LUGAR);
		String nombre = eventoJson.getString(Response.Lugar.Evento.NOMBRE);
		String fecha = eventoJson.getString(Response.Lugar.Evento.FECHA);
		String hora = eventoJson.getString(Response.Lugar.Evento.HORA);
		double total = eventoJson.getDouble(Response.Lugar.Evento.TOTAL);
		logger.debug(total);
		return new Evento(idEvento, idLugar, nombre, fecha, hora, total);
	}

}
