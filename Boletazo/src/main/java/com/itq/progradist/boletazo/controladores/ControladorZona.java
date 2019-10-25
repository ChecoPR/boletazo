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
import com.itq.progradist.boletazo.ParamNames.Metodo;
import com.itq.progradist.boletazo.ParamNames.Recurso;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoZonaTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.LugarTable;
import com.itq.progradist.boletazo.exceptions.ParamMetodoNotFoundException;
import com.itq.progradist.boletazo.modelos.Zona;

/**
 * Realiza los procesos que tienen que ver con el tipo de recurso "evento".
 * Se inicia con una conexion a la base de datos y los datos de la peticion.
 * 
 * @author Equipo 5
 *
 */
public class ControladorZona {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ControladorEvento.class);
	
	/**
	 * Conexiï¿½n a la base de datos
	 */
	private Connection conexion;
	
	/**
	 * Inicializar un controlador con una conexion a la base de datos y
	 * datos de peticion
	 * 
	 * @param conexion Conexion a la base de datos
	 * @param dataRequest Parametros de la peticion
	 */
	public ControladorZona(Connection conexion) {
		super();
		this.conexion = conexion;
	}
	
	/**
	 * Devuelve datos consultados de la base de datos segun
	 * el metodo que indiquen los parametros
	 * 
	 * @param params Parametros de la peticion, debe contener el metodo de la peticion
	 * 
	 * @return respuesta Respuesta obtenida de la base de datos
	 * 
	 * @throws ParamMetodoNotFoundException 
	 */
	public JSONObject procesarAccion(JSONObject params) throws ParamMetodoNotFoundException {
		logger.info("Procesando acción");
		JSONObject respuesta = new JSONObject();
		if(!params.has(Metodo.KEY_NAME)) {
			throw new ParamMetodoNotFoundException();
		}
		try {
			switch (params.getString(Metodo.KEY_NAME)) {
			case Metodo.Values.GET:
				logger.info("Obteniendo eventos");
				respuesta.put("data", this.getZonasDeEvento(params));
				logger.info("Eventos obtenidos");
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Metodo.KEY_NAME));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acción" + e.getMessage());
			logger.catching(e);
			respuesta.put("message", "Error en el JSON" + e.getMessage());
		} catch (JSONException e) {
			logger.error("Error en el JSON" + e.getMessage());
			logger.catching(e);
			respuesta.put("message", "Error en el JSON" + e.getMessage());
		} catch (NoIdEventoException e) {
			logger.error(e.getMessage());
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
	 * Obtiene eventos de la base de datos segï¿½n los parï¿½metros dados
	 * 
	 * @param params Parametros de bï¿½squeda de los eventos
	 * 
	 * @return respuesta Eventos que coicidieron con los parï¿½metros
	 * 
	 * @throws NoIdEventoException
	 * @throws SQLException 
	 */
	private JSONArray getZonasDeEvento(JSONObject params) throws NoIdEventoException, SQLException {
		logger.info("Iniciando consulta en la base de datos");
		Statement stmt = null;
		String sql = this.getZonasDeEventoSqlQuery(params);
		JSONArray respuesta = new JSONArray();
		stmt = this.conexion.createStatement();
		logger.info("Ejecutando consulta");
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
	         Zona zona = new Zona(
	        		 rs.getInt(EventoZonaTable.Cols.ID_LUGAR),
	        		 rs.getInt(EventoZonaTable.Cols.ID_ZONA), 
	        		 rs.getDouble(EventoZonaTable.Cols.PRECIO)
        		 );
	         Gson gson = new Gson();
	         respuesta.put(gson.toJson(zona));
		}
		logger.info("Datos obtenidos de la base de datos");
		
		return respuesta;
	}

	/**
	 * Devuelve la consulta SQL para obtener zonas de evento
	 * 
	 * @param params
	 * 
	 * @return sql
	 * 
	 * @throws NoIdEventoException
	 */
	private String getZonasDeEventoSqlQuery(JSONObject params) throws NoIdEventoException {
		String sql = "SELECT "
				+ "l." + LugarTable.Cols.ID_LUGAR 
				+ ", ez." + EventoZonaTable.Cols.ID_ZONA 
				+ ", ez." + EventoZonaTable.Cols.PRECIO
				+ " FROM " + EventoZonaTable.NAME + " ez, " + LugarTable.NAME + " l, " + EventoTable.NAME + " e"
				+ " WHERE ez." + EventoZonaTable.Cols.ID_EVENTO + " = e." + EventoTable.Cols.ID_EVENTO
				+ " AND l." + LugarTable.Cols.ID_LUGAR + " = e." + EventoTable.Cols.ID_LUGAR;
		
		if (params.has(Recurso.EventoZona.Values.ID_EVENTO)) {
			sql += " AND ez." + EventoZonaTable.Cols.ID_EVENTO + " = " + params.getInt(Recurso.EventoZona.Values.ID_EVENTO);
		} else {
			throw new NoIdEventoException("Falta el " + Recurso.EventoZona.Values.ID_EVENTO + " en la petición");
		}
		logger.debug("Consulta: " + sql);
		return sql;
	}
	
	/**
	 * Exception para cuando la peticion para
	 * obtener zonas de un evento no tiene el parametro id_evento
	 * 
	 * @author Equipo 5
	 *
	 */
	private class NoIdEventoException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Inicializa con un mensaje de error personalizado.
		 * 
		 * @param msg Mensaje de error.
		 */
		public NoIdEventoException(String msg) {
			super(msg);
		}
	}
}
