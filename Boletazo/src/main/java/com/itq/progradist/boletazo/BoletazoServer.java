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

/**
 * Realiza el proceso de inicio del servidor.
 * Abre el socket para recibir la peticiones de los clientes.
 * 
 * @author Equipo 5
 *
 */
public class BoletazoServer {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(BoletazoServer.class);
	
	/**
	 * puerto de la aplicación
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
	 * Inicia la ejecución del servidor
	 * @param args
	 */
	public static void main(String[] args) {
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
					
					logger.info("Datos recibidos de " + socket.getRemoteSocketAddress() + ":[" + input + "]");
					
					Peticion peticion = new Peticion(
							decodeString(input), 
							socket
						);
					
					peticion.start();
				}
				
				serverSocket.close();
			} catch (Exception e) {
				logger.error("Ocurrió un error en el server:");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.error("Puerto ocupado: [" + PORT + "]");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert string to JSONObject
	 * @param message Mensaje recibido del cliente
	 */
	private JSONObject decodeString(String message) {
		return new JSONObject(message);
	}
}
