package com.itq.program.dist.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.sql.*;

public class Cliente {
	
	private static Logger Logger = LogManager.getLogger(Cliente.class);
	static final String HOST = "192.168.1.1";								
	static final int PORT = 5000;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int opc = 0;
		System.out.println("busqueda por: \n1.Nombre\2.Lugar\3.Fecha\n4.Hora\n5.Precio por boleto\n6.Zona");
		switch(opc)
		{
			
		}
		JSONObject json = new JSONObject();
		json.put("method", "eventos");
		json.put("action", "get");
		String jason = json.toString();
		try
		{
			Logger.info("Inicia la ejecucion del cliente");
			//System.out.println("Inicia la ejecución del cliente");
			Socket clientSocket = new Socket(HOST, PORT);
			
			OutputStream outStream = clientSocket.getOutputStream();
			DataOutputStream flowOut = new DataOutputStream(outStream);
			flowOut.writeUTF(jason);
			
			InputStream inStream = clientSocket.getInputStream();
			DataInputStream dataIn = new DataInputStream(inStream);
			String input = dataIn.readUTF().toString();
			
			Logger.info("Respuesta del server: ["+ input +"]");
			//System.out.println("Respuesta del server: ["+ input +"]");
			clientSocket.close();
		}catch(UnknownHostException e)
		{
			Logger.error("Ocurrio un error al intentar conectarse al host: ["+ HOST +"] y puerta : [" + PORT +"]");
			//System.out.println("Ocurrio un error al intentar conectarse al host: ["+ HOST +"] y puerta: ["+ PORT +"]");
			e.printStackTrace();
		}catch(IOException e)
		{
			Logger.error("Ocurrio un error al intentar conectarse al host: ["+ HOST +"] y puerta : [" + PORT +"]");
			//System.out.println("Ocurrio un error al intentar conectarse al host: ["+ HOST +"] y puerta: ["+ PORT +"]");
			e.printStackTrace();
		}

	}

}
