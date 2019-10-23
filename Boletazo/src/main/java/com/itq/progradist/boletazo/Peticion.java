package com.itq.progradist.boletazo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.itq.progradist.boletazo.controladores.ControladorApartado;
import com.itq.progradist.boletazo.controladores.ControladorAsiento;
import com.itq.progradist.boletazo.controladores.ControladorEvento;
import com.itq.progradist.boletazo.controladores.ControladorZona;
import com.itq.progradist.boletazo.database.BoletazoDatabaseHandler;
import com.itq.progradist.boletazo.exceptions.MetodoParamNotFoundException;
import com.itq.progradist.boletazo.exceptions.RecursoParamNotFoundException;

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
	 * Corre el proceso de la petición
	 */
	@Override
	public void run() {
		try {
		
			String respuesta = procesarRequest(dataRequest);
			
			OutputStream outputStream = socket.getOutputStream();
			
			DataOutputStream flowOut = new DataOutputStream(outputStream);
			
			flowOut.writeUTF(respuesta);
			
			logger.info("La siguiente respuesta enviada al cliente " + this.socket.getRemoteSocketAddress()  + ": " + respuesta);
			
			Thread.sleep(1000);
		
		} catch (IOException e) {
			logger.error("Error al imprimir el resultado: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error en la ejecucion del hilo: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Elegir la accion a realizar segun el parametro "recurso" que 
	 * contiene los datos de la peticion.
	 * 
	 * @param params Datos de la peticion
	 */
	private String procesarRequest(JSONObject params) {
		logger.info("Procesando peticion");
		Connection conexion = BoletazoDatabaseHandler.getConnection();
		JSONObject respuesta = new JSONObject();
		try {
			if (!params.has(Recurso.KEY_NAME)) {
				throw new RecursoParamNotFoundException();
			}
			switch (params.getString(Recurso.KEY_NAME)) {
			case Recurso.Values.EVENTOS:
				logger.info("Obteniendo eventos");
				 respuesta = new ControladorEvento(conexion, params).procesarAccion(params);
				logger.info("Eventos obtenidos");
				break;
				
			case Recurso.Values.EVENTOS_ZONAS:
				logger.info("Obteniendo zonas del evento");
				respuesta = new ControladorZona(conexion, params).procesarAccion(params);
				logger.info("Zonas del evento obtenidas");
				break;
				
			case Recurso.Values.EVENTOS_ZONAS_ASIENTOS:
				logger.info("Obteniendo asientos de la zona y del evento");
				respuesta = new ControladorAsiento(conexion, params).procesarAccion(params);
				logger.info("Zonas del evento obtenidas");
				break;
			
			case Recurso.Values.APARTADO:
				logger.info("Realizando apartado");
				respuesta = new ControladorApartado(conexion, params).procesarAccion(params);
				logger.info("Apartado realizado");
				break;
				
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Recurso.KEY_NAME));
			}
		} catch(IllegalArgumentException e) {
			logger.error("Error al procesar la peticion: " + e.getMessage());
		} catch (MetodoParamNotFoundException e) {
			logger.error(e.getMessage());
			respuesta.put("message", e.getMessage());
			e.printStackTrace();
		} catch (RecursoParamNotFoundException e) {
			logger.error(e.getMessage());
			respuesta.put("message", e.getMessage());
			e.printStackTrace();
		}
		cerrarConexion(conexion);
		return respuesta.toString();
	}
	
	/**
	 * Cierra la conexion a la base de datos
	 * 
	 * @param conexion Conexion a la base de datos
	 */
	private void cerrarConexion(Connection conexion) {
		try {
			logger.info("Cerrando conexion a la base de datos");
			conexion.close();
			logger.info("Conexion a la base de datos cerrada");
		} catch (SQLException e) {
			logger.error("Error al cerrar la base de datos: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
