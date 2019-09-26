package com.itq.progradist.boletazo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.itq.progradist.boletazo.controladores.ControladorEvento;

public class BoletazoServer {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(BoletazoServer.class);
	
	/**
	 * puerto de la aplicaci�n
	 */
	private static final int PORT = 5000;
	
	/**
	 * Tiempo de reserva de un boleto
	 */
	private static final double RESERVED_TIME = 1000000;
	
	/**
	 * Conexion a la base de datos
	 */
	private Connection conexion;
	
	/**
	 * Inicia la ejecuci�n del servidor
	 * @param args
	 */
	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		json.put("method", "eventos");
		json.put("action", "get");
		json.toString();
		new BoletazoServer().initSocket();

	}
	
	/**
	 * Iniciar el socket del servidor
	 */
	private void initSocket() {
		ServerSocket serverSocket;
		
		boolean alive = true;
		
		try {
			
			serverSocket = new ServerSocket(PORT);
			logger.info("Servidor iniciado existosamente en el puerto [" + PORT + "]");
			
			try {
				
				while(alive) {
					
					logger.info("Inicia while...");
					
					Socket socket = serverSocket.accept();
					
					InputStream inputStream = socket.getInputStream();
					
					DataInputStream dataIn = new DataInputStream(inputStream);
					
					String input = dataIn.readUTF().toString();
					
					logger.debug("Datos recibidos:[" + input + "]");
					
					JSONObject dataRequest = decodeString(input);
					
					String respuesta = procesarRequest(dataRequest);
					
					OutputStream outputStream = socket.getOutputStream();
					
					DataOutputStream flowOut = new DataOutputStream(outputStream);
					
					flowOut.writeUTF("Respuesta " + respuesta);
					
					System.out.println("Respuesta enviada");
					
					Thread.sleep(1000);
					
				}
				
				serverSocket.close();
			} catch (Exception e) {
				logger.error("Ocurri� un error en el server:");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.error("Puerto ocupado: [" + PORT + "]");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Connection getConnection() {
		String HOST = "192.168.1.2";
		String PORT = "3306";
		String DATABASE = "boletazo";
		String USER = "boletazo";
		String PASSWORD = "password";
		String CONNECTION_PARAMS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		Connection conexion;
		try {
			conexion = DriverManager.getConnection(
					"jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + CONNECTION_PARAMS,
					USER,
					PASSWORD
			);
			logger.info("Conectado exitosamente a la base de datos");
//			conexion.close();
			return conexion;
		} catch (SQLException e) {
			logger.error("Error al conectar con la base de datos: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Convert string to JSONObject
	 * @param message
	 */
	private JSONObject decodeString(String message) {
		return new JSONObject(message);
	}
	
	/**
	 * Elegir la acci�n a realizar seg�n los parametros recibidos
	 * @param params
	 */
	private String procesarRequest(JSONObject params) {
		logger.info("Procesando petici�n");
		Connection conexion = getConnection();
		try {
			switch (params.getString("recurso")) {
			case "eventos":
				logger.info("Obteniendo eventos");
				JSONObject respuesta = new ControladorEvento(conexion, params).procesarAccion(params);
				logger.info("Eventos obtenidos");
				return respuesta.toString();

			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get("recurso"));
			}
		} catch(IllegalArgumentException e) {
			logger.info("Error al procesar la petici�n: " + e.getMessage());
		}
		return null;
	}

}
