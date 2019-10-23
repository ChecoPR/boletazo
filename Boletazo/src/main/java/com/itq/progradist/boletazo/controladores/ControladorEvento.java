package com.itq.progradist.boletazo.controladores;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.ParamNames.Metodo;
import com.itq.progradist.boletazo.exceptions.MetodoParamNotFoundException;
import com.itq.progradist.boletazo.modelos.Evento;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Realiza los procesos que tienen que ver con el tipo de recurso "evento".
 * Se inicia con una conexion a la base de datos y los datos de la peticion.
 * 
 * @author Equipo 5
 *
 */
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
	 * 
	 * @return respuesta Respuesta obtenida de la base de datos
	 * 
	 * @throws MetodoParamNotFoundException 
	 */
	public JSONObject procesarAccion(JSONObject params) throws MetodoParamNotFoundException {
		logger.info("Procesando acción");
		JSONObject respuesta = new JSONObject();
		if(!params.has(Metodo.KEY_NAME)) {
			throw new MetodoParamNotFoundException();
		}
		try {
			switch (params.getString(Metodo.KEY_NAME)) {
			case Metodo.Values.GET:
				logger.info("Obteniendo eventos");
				respuesta.put("data", this.getEventos(params));
				logger.info("Eventos obtenidos");
				return respuesta;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Metodo.KEY_NAME));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acción" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Obtener un evento dado su ID
	 * 
	 * @param idEvento ID del evento a buscar
	 * 
	 * @return
	 */
	private Evento getEvento(int idEvento) {
		return null;
	}
	
	/**
	 * Obtiene eventos de la base de datos según los parámetros dados
	 * 
	 * @param params Parametros de búsqueda de los eventos
	 * 
	 * @return respuesta Eventos que coicidieron con los parámetros
	 */
	private JSONArray getEventos(JSONObject params) {
		logger.info("Iniciando consulta en la base de datos");
		Statement stmt = null;
		String sql = getEventosSqlQuery(params);
		JSONArray respuesta = new JSONArray();
		try {
			stmt = this.conexion.createStatement();
			logger.info("Ejecutando consulta");
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
		         Evento evento = new Evento(
		        		 rs.getInt("idEvento"), 
		        		 rs.getInt("idLugar"), 
		        		 rs.getString("nombre"),
		        		 rs.getString("fecha"),
		        		 rs.getString("hora")
	        		 );
		         Gson gson = new Gson();
		         respuesta.put(gson.toJson(evento));
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
	 * Devuelve la consulta SQL de los eventos que
	 * cumplan con lo parámetros seleccionados
	 * 
	 * @param params Parámteros de la consulta
	 * 
	 * @return sql Consulta a la base de datos
	 */
	private String getEventosSqlQuery(JSONObject params) {
		
		String sql = "SELECT Eventos.* FROM Eventos, EventosZonas, Lugar"
				+ " WHERE Eventos.idEvento = EventosZonas.idEvento"
				+ " AND Lugar.idLugar = Eventos.idLugar";
		
		if (params.has("nombre")) {
			sql += " AND Eventos.nombre LIKE '%" + params.getString("nombre") + "%'";
		}
		if (params.has("lugar")) {
			sql += " AND Lugar.nombre LIKE '%" + params.getString("lugar") + "%'";
		}
		if (params.has("estado")) {
			sql += " AND Lugar.estado LIKE '%" + params.getString("estado") + "%'";
		}
		if (params.has("fecha")) {
			sql += " AND Eventos.fecha LIKE '%" + params.getString("fecha") + "%'";
		}
		if (params.has("hora")) {
			sql += " AND Eventos.hora LIKE '%" + params.getString("hora") + "%'";
		}
		if (params.has("precio")) {
			sql += " AND EventosZonas.precio LIKE '%" + params.getDouble("precio") + "%'";
		}
		
		sql += " GROUP BY Eventos.idEvento";
		
		return sql;
	}
	
}
