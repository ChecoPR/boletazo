package com.itq.program.dist.cliente.apartado;

import java.util.ArrayList;

import org.json.JSONObject;

public class PruebaPagar {

	public static void main(String[] args) {
		ArrayList<String> peticiones = new ArrayList<String>();
		
		JSONObject peticion1 = new JSONObject();
		peticion1.put("recurso", "pago");
		peticion1.put("metodo", "post");
		peticion1.put("metodo_pago", 1);
		peticion1.put("apartado_id", 46);
		
		peticiones.add(peticion1.toString());
		for (int i = 0; i < peticiones.size(); i++) {
			new Cliente(peticiones.get(i)).start();
		}

	}

}
