package com.itq.progradist.boletazo.controladores;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.ParamNames.Recurso;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoZonaTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.LugarTable;
import com.itq.progradist.boletazo.ParamNames.Metodo;
import com.itq.progradist.boletazo.exceptions.ParamMetodoNotFoundException;
import com.itq.progradist.boletazo.modelos.Evento;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.itq.progradist.boletazo.database.DatabaseSchema.EventoTable;

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
	 * Conexi�n a la base de datos
	 */
	private Connection conexion;
	
	/**
	 * Inicializar un controlador con una conexi�n a la base de datos y
	 * datos de petici�n
	 * 
	 * @param conexion Conexi�n a la base de datos
	 * @param dataRequest Par�metros de la petici�n
	 */
	public ControladorEvento(Connection conexion) {
		super();
		this.conexion = conexion;
	}

	/**
	 * Devuelve datos consultados de la base de datos seg�n
	 * el m�todo que indiquen los par�metros
	 * 
	 * @param params Par�metros de la petici�n, debe contener el m�todo de la petici�n
	 * 
	 * @return respuesta Respuesta obtenida de la base de datos
	 * 
	 * @throws ParamMetodoNotFoundException 
	 */
	public JSONObject procesarAccion(JSONObject params) throws ParamMetodoNotFoundException {
		logger.info("Procesando acci�n");
		JSONObject respuesta = new JSONObject();
		if(!params.has(Metodo.KEY_NAME)) {
			throw new ParamMetodoNotFoundException();
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
			logger.error("Error procesando la acci�n" + e.getMessage());
			logger.catching(e);
			respuesta.put("message", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al consultar la base de datos: " + e.getMessage());
			logger.catching(e);
			respuesta.put("message", "Error al consultar la base de datos");
		}
		return respuesta;
	}
	
	/**
	 * Obtener un evento dado su ID
	 * 
	 * @param idEvento ID del evento a buscar
	 * 
	 * @return
	 */
//	private Evento getEvento(int idEvento) {
//		return null;
//	}
	
	/**
	 * Obtiene eventos de la base de datos seg�n los par�metros dados
	 * 
	 * @param params Parametros de b�squeda de los eventos
	 * 
	 * @return respuesta Eventos que coicidieron con los par�metros
	 * @throws SQLException 
	 */
	private JSONArray getEventos(JSONObject params) throws SQLException {
		logger.info("Iniciando consulta en la base de datos");
		Statement stmt = null;
		String sql = getEventosSqlQuery(params);
		JSONArray respuesta = new JSONArray();
		stmt = this.conexion.createStatement();
		logger.info("Ejecutando consulta");
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
	         Evento evento = new Evento(
	        		 rs.getInt(EventoTable.Cols.ID_EVENTO), 
	        		 rs.getInt(EventoTable.Cols.ID_LUGAR), 
	        		 rs.getString(EventoTable.Cols.NOMBRE),
	        		 rs.getString(EventoTable.Cols.FECHA),
	        		 rs.getString(EventoTable.Cols.HORA)
        		 );
	         Gson gson = new Gson();
	         respuesta.put(gson.toJson(evento));
		}
		logger.info("Datos obtenidos de la base de datos");
		return respuesta;
	}
	
	/**
	 * Devuelve la consulta SQL de los eventos que
	 * cumplan con lo par�metros seleccionados
	 * 
	 * @param params Par�mteros de la consulta
	 * 
	 * @return sql Consulta a la base de datos
	 */
	private String getEventosSqlQuery(JSONObject params) {
		
		String sql = "SELECT e.*"
				+ " FROM " + EventoTable.NAME + " e, " + EventoZonaTable.NAME + " ez, " + LugarTable.NAME + " l"
				+ " WHERE e." + EventoTable.Cols.ID_EVENTO + " = ez.idEvento"
				+ " AND l." + LugarTable.Cols.ID_LUGAR + " = e." + EventoTable.Cols.ID_LUGAR;
		
		if (params.has(Recurso.Evento.Values.NOMBRE)) {
			sql += " AND e." + EventoTable.Cols.NOMBRE + " LIKE '%" + params.getString(Recurso.Evento.Values.NOMBRE) + "%'";
		}
		if (params.has(Recurso.Evento.Values.LUGAR)) {
			sql += " AND l." + LugarTable.Cols.NOMBRE + " LIKE '%" + params.getString(Recurso.Evento.Values.LUGAR) + "%'";
		}
		if (params.has(Recurso.Evento.Values.ESTADO)) {
			sql += " AND l." + LugarTable.Cols.ESTADO + " LIKE '%" + params.getString(Recurso.Evento.Values.ESTADO) + "%'";
		}
		if (params.has(Recurso.Evento.Values.FECHA)) {
			sql += " AND e." + EventoTable.Cols.FECHA + " LIKE '%" + params.getString(Recurso.Evento.Values.FECHA) + "%'";
		}
		if (params.has(Recurso.Evento.Values.HORA)) {
			sql += " AND e." + EventoTable.Cols.HORA + " LIKE '%" + params.getString(Recurso.Evento.Values.HORA) + "%'";
		}
		if (params.has(Recurso.Evento.Values.PRECIO)) {
			sql += " AND ez." + EventoZonaTable.Cols.PRECIO + " LIKE '%" + params.getDouble(Recurso.Evento.Values.PRECIO) + "%'";
		}
		
		sql += " GROUP BY e." + EventoTable.Cols.ID_EVENTO;
		
		logger.debug("Consulta: " + sql);
		
		return sql;
	}
	
}
