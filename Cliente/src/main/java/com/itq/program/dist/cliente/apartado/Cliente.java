package com.itq.program.dist.cliente.apartado;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cliente extends Thread {
	
	private static final Logger logger = LogManager.getLogger(Cliente.class);
	
	/*
	 * Defines remote host
	 */
	static final String HOST = "localhost";
	
	/*
	 * Defines remote host
	 */
	static final int PORT = 5000;
	
	private String json;
	
	public Cliente(String json) {
		this.json = json;
	}
	
	@Override
	public void run() {
		try {
			
			logger.info("Inicia la ejecución del cliente.");
			
			Socket clientSocket = new Socket(HOST, PORT);
			
			OutputStream outputStream = clientSocket.getOutputStream();
			
			DataOutputStream flowOut = new DataOutputStream(outputStream);
			
			flowOut.writeUTF(this.json);
			
			InputStream inputStream = clientSocket.getInputStream();
			
			DataInputStream dataIn = new DataInputStream(inputStream);
			
			String input = dataIn.readUTF().toString();
			
			logger.info("Respuesta del server: [" + input + "]");
			
			clientSocket.close();
			
		} catch (UnknownHostException e) {
			
			logger.error("Ocurrió un error al intentar conectarse al host: [" + HOST + "]");
			
		} catch (IOException e) {
			
			logger.error("Ocurrió un error al establecer el canal de datos: [" + HOST + "] y puerto: [" + PORT + "]");
			
		}
	}
}
