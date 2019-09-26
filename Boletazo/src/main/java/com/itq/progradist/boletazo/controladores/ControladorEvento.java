package com.itq.progradist.boletazo.controladores;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.modelos.Asiento;
import com.itq.progradist.boletazo.modelos.Evento;
import com.itq.progradist.boletazo.modelos.Zona;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ControladorEvento {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ControladorEvento.class);
	
	/**
	 * Conexión a la base de datos
	 */
	private Connection conexion;
	
	/**
	 * datos de la petición
	 */
	private JSONObject dataRequest;
	
	/**
	 * Inicializar un controlador con una conexión a la base de datos y
	 * datos de petición
	 * 
	 * @param conexion Conexión a la base de datos
	 * @param dataRequest Parámetros de la petición
	 */
	public ControladorEvento(Connection conexion, JSONObject dataRequest) {
		super();
		this.conexion = conexion;
		this.dataRequest = dataRequest;
	}

	/**
	 * Devuelve datos consultados de la base de datos según
	 * el método que indiquen los parámetros
	 * 
	 * @param params Parámetros de la petición, debe contener el método de la petición
	 * @return respuesta Respuesta obtenida de la base de datos
	 */
	public JSONObject procesarAccion(JSONObject params) {
		logger.info("Procesando acción");
		JSONObject respuesta = new JSONObject();
		try {
			switch (params.getString("method")) {
			case "get":
				logger.info("Obteniendo eventos");
				respuesta.put("data", this.getEventos(params));
				logger.info("Eventos obtenidos");
				return respuesta;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get("method"));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acción" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 
	 * @param idZona
	 * @return
	 */
	private ArrayList<Asiento> getAsientos(int idZona) {
		return null;
	}
	
	/**
	 * 
	 * @param idEvento
	 * @return
	 */
	private Evento getEvento(int idEvento) {
		return null;
	}
	
	/**
	 * Obtiene eventos de la base de datos según los parámetros dados
	 * 
	 * @param params Parametros de búsqueda de los eventos
	 * @return respuesta Eventos que coicidieron con los parámetros
	 */
	private JSONArray getEventos(JSONObject params) {
		logger.info("Iniciando consulta en la base de datos");
		Statement stmt = null;
		String sql = "SELECT * FROM Eventos";
		JSONArray respuesta = new JSONArray();
		try {
			stmt = this.conexion.createStatement();
			logger.info("Ejecutando consulta");
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
		         Evento evento = new Evento(
		        		 rs.getInt("idEvento"), 
		        		 rs.getInt("idLugar"), 
		        		 rs.getString("nombre")
	        		 );
		         respuesta.put(evento);
			}
			logger.info("Datos obtenidos de la base de datos");
			return respuesta;
		} catch (SQLException e) {
			logger.error("Error al consultar la base de datos: " + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param idLugar
	 * @return
	 */
	private ArrayList<Zona> getZonas(int idLugar) {
		return null;
	}
	
}
