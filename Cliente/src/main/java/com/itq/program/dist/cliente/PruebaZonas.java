package com.itq.program.dist.cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.itq.program.dist.cliente.Cliente.Excepcion;

public class PruebaZonas {

	private static Logger Logger = LogManager.getLogger(Cliente.class);
	static final String HOST = "localhost";								
	static final int PORT = 5000;
	static private ArrayList<String> filtros_disp = new ArrayList<String>(); //Filtros disponibles a seleccionar
	
	/**
	 * Devuelve una conexi�n a la base de datos
	 * 
	 * @return conexi�n Conexi�n a la base de datos
	 * @throws Excepcion 
	 */
	
	public static void main(String[] args) throws Excepcion {
		// TODO Auto-generated method stub

		Scanner rd = new Scanner(System.in);
		JSONObject json = new JSONObject();
		
		try
		{
			
			Logger.info("Inicia la ejecucion del cliente");
			Socket clientSocket = new Socket(HOST, PORT);
			
			System.out.print("Ingrese id de evento: ");
			int id = rd.nextInt();
			
			json = new JSONObject();
			json.put("recurso", "evento/zonas");
			json.put("metodo", "get");
			json.put("id_evento", id);
			String jason = json.toString();
			System.out.print(jason);
			
			OutputStream outStream = clientSocket.getOutputStream();
			DataOutputStream flowOut = new DataOutputStream(outStream);
			flowOut.writeUTF(jason);
			
			InputStream inStream = clientSocket.getInputStream();
			DataInputStream dataIn = new DataInputStream(inStream);
			String input = dataIn.readUTF().toString();
			
			Logger.info("Respuesta del server: ["+ input +"]");
			
			clientSocket.close();
		}catch(UnknownHostException e)
		{
			Logger.error("Ocurrio un error al intentar conectarse al host: ["+ HOST +"] y puerta : [" + PORT +"]");
			e.printStackTrace();
		}catch(IOException e)
		{
			Logger.error("Ocurrio un error al intentar conectarse al host: ["+ HOST +"] y puerta : [" + PORT +"]");
			e.printStackTrace();
		}/*catch(Excepcion ex)
		{
			System.out.println(ex.getMessage());
		}catch(IndexOutOfBoundsException e) {
			Logger.error("El filtro seleccionado no existe...");
			e.printStackTrace();
		}*/
		rd.close();
	}
	
	private static void llenarLista()
	{
		filtros_disp.add("Nombre");
		filtros_disp.add("Lugar");
		filtros_disp.add("Fecha");
		filtros_disp.add("Hora");
		filtros_disp.add("Precio");
		filtros_disp.add("Zona");
		filtros_disp.add("Estado");
	}
	private static void mostrarOpcion()
	{
		System.out.println();
		System.out.println("Seleccione un filtro: ");
		for(int i = 0;i < filtros_disp.size();i++)
		{
			System.out.println((i + 1) + ".- " + filtros_disp.get(i));
		}
	}
	
	private static String leer()
	{
		Scanner str = new Scanner(System.in);
		String valor = str.nextLine();
		return valor;
	}
	
	private static String decodeJSON(String input)
	{
		String mensaje;
		JSONObject jsonObject = new JSONObject(input);
		JSONObject newJSON = jsonObject.getJSONObject("stat");
		mensaje = newJSON.toString();
        return mensaje;
	}
	
	public static class Excepcion extends Exception
	{
		private int codigoError;
	     
	    public Excepcion(int codigoError)
	    {
	        super();
	        this.codigoError = codigoError;
	    }
	    
	    public String getMessage()
	    {
	         
	        String mensaje="";
	         
	        switch(codigoError)
	        {
	            case 1:
	            	Logger.error("La opcion seleccionada es incorrecta...");
	                mensaje = "La opcion seleccionada es incorrecta...";
	                break;
	        } 
	        return mensaje;
	    }
	}

}
