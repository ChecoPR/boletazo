package com.itq.progradist.boletazo.controladores;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.exceptions.MetodoParamNotFoundException;
import com.itq.progradist.boletazo.modelos.Asiento;

public class ControladorAsiento {
	
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
	public ControladorAsiento(Connection conexion, JSONObject dataRequest) {
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
	 * @throws MetodoParamNotFoundException 
	 */
	public JSONObject procesarAccion(JSONObject params) throws MetodoParamNotFoundException {
		logger.info("Procesando acción");
		JSONObject respuesta = new JSONObject();
		if(!params.has("metodo")) {
			throw new MetodoParamNotFoundException();
		}
		try {
			switch (params.getString("metodo")) {
			case "get":
				logger.info("Obteniendo eventos");
				respuesta.put("data", this.getAsientosDeZonaYEvento(params));
				logger.info("Eventos obtenidos");
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get("method"));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acción" + e.getMessage());
		} catch (JSONException e) {
			logger.error("Error en el JSON" + e.getMessage());
			e.printStackTrace();
		} catch (NoIdEventoException e) {
			logger.error(e.getMessage());
			respuesta.put("message", e.getMessage());
			e.printStackTrace();
		}
		return respuesta;
	}
	
	/**
	 * Obtiene eventos de la base de datos según los parámetros dados
	 * 
	 * @param params Parametros de búsqueda de los eventos
	 * @return respuesta Eventos que coicidieron con los parámetros
	 * @throws NoIdEventoException
	 */
	private JSONArray getAsientosDeZonaYEvento(JSONObject params) throws NoIdEventoException {
		logger.info("Iniciando consulta en la base de datos");
		Statement stmt = null;
		String sql = this.getAsientosDeEventoYZonaSqlQuery(params);
		JSONArray respuesta = new JSONArray();
		try {
			stmt = this.conexion.createStatement();
			logger.info("Ejecutando consulta");
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
		         Asiento asiento = new Asiento(
		        		 rs.getBoolean("estado"), 
		        		 rs.getInt("idAsiento"),
		        		 rs.getInt("idZona")
	        		 );
		         Gson gson = new Gson();
		         respuesta.put(gson.toJson(asiento));
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
	 * Devuelve la consulta SQL para obtener zonas de evento
	 * 
	 * @param params
	 * @return sql
	 * @throws NoIdEventoException
	 */
	private String getAsientosDeEventoYZonaSqlQuery(JSONObject params) throws NoIdEventoException {
		String sql = "SELECT Asientos.* FROM Asientos, Zona, Lugar, Eventos"
				+ " WHERE Asientos.idZona = Zona.idZona"
				+ " AND Zona.idLugar = Lugar.idLugar"
				+ " AND Lugar.idEvento = Eventos.idEvento";
		
		if (params.has("id_evento")) {
			sql += " AND Eventos.idEvento = " + params.getInt("id_evento");
		} else {
			throw new NoIdEventoException("Falta el id de evento en la petición");
		}
		
		if (params.has("id_zona")) {
			sql += " AND Zona.idZona = " + params.getInt("id_zona");
		} else {
			throw new NoIdEventoException("Falta el id de zona en la petición");
		}
		return sql;
	}
	
	/**
	 * Exception para cuando la petición para
	 * obtener zonas de un evento no tiene el parámetro id_evento
	 * @author arman
	 *
	 */
	private class NoIdEventoException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NoIdEventoException(String msg) {
			super(msg);
		}
	}
}
