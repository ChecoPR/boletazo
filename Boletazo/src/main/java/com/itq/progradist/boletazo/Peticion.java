package com.itq.progradist.boletazo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.itq.progradist.boletazo.controladores.ControladorAsiento;
import com.itq.progradist.boletazo.controladores.ControladorEvento;
import com.itq.progradist.boletazo.controladores.ControladorZona;
import com.itq.progradist.boletazo.exceptions.MetodoParamNotFoundException;
import com.itq.progradist.boletazo.exceptions.RecursoParamNotFoundException;

public class Peticion extends Thread {
	
	/**
	 * logger de la clase Peticion
	 */
	private static final Logger logger = LogManager.getLogger(Peticion.class);
	
	/**
	 * datos de la petici�n
	 */
	private JSONObject dataRequest;
	
	/**
	 * flujo de salida del socket del servidor
	 */
	private OutputStream socketOutput;
	
	/**
	 * Inicializa una petici�n con los datos de petici�n y 
	 * el flujo de salida del socket
	 * 
	 * @param dataRequest
	 * @param socketOutput
	 */
	public Peticion(JSONObject dataRequest, OutputStream socketOutput) {
		this.dataRequest = dataRequest;
		this.socketOutput = socketOutput;
	}
	
	/**
	 * Corre el proceso de la petici�n
	 */
	@Override
	public void run() {
		try {
		
			String respuesta = procesarRequest(dataRequest);
			
			DataOutputStream flowOut = new DataOutputStream(socketOutput);
			
			flowOut.writeUTF(respuesta);
			
			System.out.println("Respuesta enviada");
			
			Thread.sleep(1000);
		
		} catch (IOException e) {
			logger.error("Error al imprimir el resultado: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("Error en la ejecuci�n del hilo: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Devuelve una conexi�n a la base de datos
	 * 
	 * @return conexi�n Conexi�n a la base de datos
	 */
	private Connection getConnection() {
		String HOST = "localhost";
		String PORT = "3306";
		String DATABASE = "boletazo";
		String USER = "boletazo";
		String PASSWORD = "password";
		String CONNECTION_PARAMS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		Connection conexion;
		try {
			conexion = DriverManager.getConnection(
					"jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + CONNECTION_PARAMS,USER,PASSWORD
			);
			logger.info("Conectado exitosamente a la base de datos");
			return conexion;
		} catch (SQLException e) {
			logger.error("Error al conectar con la base de datos: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Elegir la acci�n a realizar seg�n los parametros recibidos
	 * 
	 * @param params Datos de la petici�n
	 */
	private String procesarRequest(JSONObject params) {
		logger.info("Procesando petici�n");
		Connection conexion = getConnection();
		JSONObject respuesta = new JSONObject();
		try {
			if (!params.has("recurso")) {
				throw new RecursoParamNotFoundException();
			}
			switch (params.getString("recurso")) {
			case "eventos":
				logger.info("Obteniendo eventos");
				 respuesta = new ControladorEvento(conexion, params).procesarAccion(params);
				logger.info("Eventos obtenidos");
				break;
				
			case "evento/zonas":
				logger.info("Obteniendo zonas del evento");
				respuesta = new ControladorZona(conexion, params).procesarAccion(params);
				logger.info("Zonas del evento obtenidas");
				break;
				
			case "evento/zonas/asientos":
				logger.info("Obteniendo asientos de la zona y del evento");
				respuesta = new ControladorAsiento(conexion, params).procesarAccion(params);
				logger.info("Zonas del evento obtenidas");
				break;
				
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get("recurso"));
			}
		} catch(IllegalArgumentException e) {
			logger.error("Error al procesar la petici�n: " + e.getMessage());
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
	 * Cierra la conexi�n a la base de datos
	 * 
	 * @param conexion Conexi�n a la base de datos
	 */
	private void cerrarConexion(Connection conexion) {
		try {
			logger.info("Cerrando conexi�n a la base de datos");
			conexion.close();
			logger.info("Conexi�n a la base de datos cerrada");
		} catch (SQLException e) {
			logger.error("Error al cerrar la base de datos: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
