package com.itq.program.dist.cliente.apartado;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class PruebaApartar {

//	private static final Logger logger = LogManager.getLogger(Main.class);

	/*
	 * Client socket main method. Requests a connection, sends a message and receives an answer
	 */
	public static void main(String[] args) {
		ArrayList<String> peticiones = new ArrayList<String>();
		JSONObject boleto = new JSONObject();
		boleto.put("asiento_id", 1);
		
		JSONArray boletos = new JSONArray();
		boletos.put(boleto);
		
		JSONObject peticion1 = new JSONObject();
		peticion1.put("recurso", "apartado");
		peticion1.put("metodo", "post");
		peticion1.put("evento_id", 2);
		peticion1.put("usuario_id", 1);
		peticion1.put("zona_id", 1);
		peticion1.put("num_boletos", boletos);
		
		JSONObject peticion2 = new JSONObject();
		peticion2.put("recurso", "apartado");
		peticion2.put("metodo", "post");
		peticion2.put("evento_id", 2);
		peticion2.put("usuario_id", 2);
		peticion2.put("zona_id", 1);
		peticion2.put("num_boletos", boletos);
		
		peticiones.add(peticion1.toString());
		peticiones.add(peticion2.toString());
		for (int i = 0; i < peticiones.size(); i++) {
			new Cliente(peticiones.get(i)).start();
		}
	}

}
