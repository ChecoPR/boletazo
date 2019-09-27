package com.itq.progradist.boletazo.controladores;

import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.modelos.Boleto;

import java.util.ArrayList;

public class ControladorApartado {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ControladorApartado.class);
	
	/**
	 * conexion a la base de datos
	 */
	private Connection conexion;
	
	/**
	 * datos recibidos de la petición
	 */
	private JSONObject dataRequest;
	
	
	public ControladorApartado(Connection conexion, JSONObject dataRequest) {
		super();
		try {
			this.conexion = DriverManager.getConnection("", "", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dataRequest = dataRequest;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject procesarAccion(JSONObject params) {
		return null;
	}

	/**
	 * 
	 * @param idApartado
	 * @return
	 */
	private ArrayList<Boleto> getBoletos(int idApartado) {
		return null;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	private ArrayList<Apartado> guardarApartado(JSONObject params) {
		return null;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	private JSONArray hacerPago(JSONObject params) {
		return null;
	}
}
