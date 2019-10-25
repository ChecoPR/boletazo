package com.itq.progradist.boletazo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.itq.progradist.boletazo.controladores.ControladorApartado;
import com.itq.progradist.boletazo.controladores.ControladorAsiento;
import com.itq.progradist.boletazo.controladores.ControladorEvento;
import com.itq.progradist.boletazo.controladores.ControladorPago;
import com.itq.progradist.boletazo.controladores.ControladorZona;
import com.itq.progradist.boletazo.database.DatabaseHandler;
import com.itq.progradist.boletazo.exceptions.ParamMetodoNotFoundException;
import com.itq.progradist.boletazo.exceptions.ParamRecursoNotFoundException;

import static com.itq.progradist.boletazo.ParamNames.*;

/**
 * Maneja el proceso para servir la peticion del cliente.
 * Se inicializa con los datos de la peticion, elige el proceso 
 * que se realizara dependiendo del parametro recurso de la peticion.
 * 
 * @author Equipo 5
 *
 */
public class Peticion extends Thread {
	
	/**
	 * logger de la clase Peticion
	 */
	private static final Logger logger = LogManager.getLogger(Peticion.class);
	
	/**
	 * datos de la petición
	 */
	private JSONObject dataRequest;
	
	/**
	 * socket que atiende la petición del cliente
	 */
	private Socket socket;
	
	/**
	 * Inicializa una petición con los datos de petición y 
	 * el flujo de salida del socket
	 * 
	 * @param dataRequest Datos de la peticion
	 * @param socketOutput Flujo de salida del servidor
	 */
	public Peticion(JSONObject dataRequest, Socket socket) {
		this.dataRequest = dataRequest;
		this.socket = socket;
	}
	
	/**
	 * Ejecuta el proceso de la petición
	 */
	@Override
	public void run() {
		try {
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream flowOut = new DataOutputStream(outputStream);
			String respuesta = procesarRequest(dataRequest);
			
			flowOut.writeUTF(respuesta);
			
			logger.info("La siguiente respuesta fue enviada al cliente " + this.socket.getRemoteSocketAddress()  + ": " + respuesta);
		
		} catch (IOException e) {
			logger.error("Error al imprimir el resultado: " + e.getMessage());
			logger.catching(e);
		}
	}
	
	/**
	 * Obtiene una conexión a la base de datos y 
	 * devuelve una respuesta según los datos de la petición.
	 * 
	 * Si hay un error en los datos de la petición se devuelve 
	 * como respuesta el mensaje de error correspondiente.
	 * 
	 * @param params Datos de la petición
	 */
	private String procesarRequest(JSONObject params) {
		logger.info("Procesando peticion");
		
		Connection conexion = DatabaseHandler.getConnection();
		
		JSONObject respuesta = new JSONObject();
		try {
			
			respuesta = obtenerRespuesta(params, conexion);
			
		} catch(IllegalArgumentException e) {
			logger.error("Error al procesar la peticion: " + e.getMessage());
			respuesta.put("message", e.getMessage());
		} catch (ParamMetodoNotFoundException e) {
			logger.error(e.getMessage());
			respuesta.put("message", e.getMessage());
		} catch (ParamRecursoNotFoundException e) {
			logger.error(e.getMessage());
			respuesta.put("message", e.getMessage());
		}
		
		DatabaseHandler.cerrarConexion(conexion);
		
		return respuesta.toString();
	}
	
	/**
	 * Elegir la acción a realizar según el parámetro "recurso" que 
	 * contienen los datos de la petición.
	 * 
	 * @param params Datos de la petición
	 * @conexion params Conexión a la base de datos
	 */
	private JSONObject obtenerRespuesta(JSONObject params, Connection conexion) throws ParamRecursoNotFoundException, ParamMetodoNotFoundException {
		JSONObject respuesta;
		
		if (!params.has(Recurso.KEY_NAME)) {
			throw new ParamRecursoNotFoundException();
		}
		
		switch (params.getString(Recurso.KEY_NAME)) {
			case Recurso.Evento.VALUE:
				logger.info("Obteniendo eventos");
				respuesta = new ControladorEvento(conexion).procesarAccion(params);
				logger.info("Eventos obtenidos");
				return respuesta;
				
			case Recurso.EventoZona.VALUE:
				logger.info("Obteniendo zonas del evento");
				respuesta = new ControladorZona(conexion).procesarAccion(params);
				logger.info("Zonas del evento obtenidas");
				return respuesta;
				
			case Recurso.EventoZonaAsiento.VALUE:
				logger.info("Obteniendo asientos de la zona y del evento");
				respuesta = new ControladorAsiento(conexion).procesarAccion(params);
				logger.info("Zonas del evento obtenidas");
				return respuesta;
			
			case Recurso.Apartado.VALUE:
				logger.info("Realizando apartado");
				respuesta = new ControladorApartado(conexion).procesarAccion(params);
				logger.info("Apartado realizado");
				return respuesta;
				
			case Recurso.Pago.VALUE:
				logger.info("Realizando pago");
				respuesta = new ControladorPago(conexion).procesarAccion(params);
				logger.info("Pago realizado");
				return respuesta;
				
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Recurso.KEY_NAME));
		}
	}
}
