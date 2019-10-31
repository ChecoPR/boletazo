package com.itq.progradist.boletazo.controladores;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.ParamNames;
import com.itq.progradist.boletazo.ParamNames.Metodo;
import com.itq.progradist.boletazo.database.CommonQueries;
import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoAsientoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoZonaTable;
import com.itq.progradist.boletazo.exceptions.ParamMetodoNotFoundException;
import com.itq.progradist.boletazo.modelos.Evento;
import com.itq.progradist.boletazo.modelos.Lugar;

public class ControladorLugar {
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ControladorPago.class);
	
	/**
	 * Conexión a la base de datos
	 */
	private Connection conexion;
	
	/**
	 * Inicializar un controlador con una conexión a la base de datos y
	 * datos de petición
	 * 
	 * @param conexion Conexión a la base de datos
	 * @param dataRequest Parámetros de la petición
	 */
	public ControladorLugar(Connection conexion) {
		super();
		this.conexion = conexion;
	}

	/**
	 * Devuelve datos consultados de la base de datos según
	 * el método que indiquen los parámetros
	 * 
	 * @param params Parámetros de la petición, debe contener el método de la petición
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
			case ParamNames.Metodo.Values.GET:
				logger.info("Obtiendo ventas de las sedes");
				String today = CommonQueries.getDatabaseCurrentDate(this.conexion);
				respuesta.put("day", today);
				respuesta.put("data", this.getVentas(params));
				logger.info("Ventas de las sedes obtenidas");
				return respuesta;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Metodo.KEY_NAME));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acción" + e.getMessage());
			logger.catching(e);
			respuesta.put("message", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al consultar la base de datos: " + e.getMessage());
			logger.catching(e);
			respuesta.put("message", "Error al consultar la base de datos");
		}
		return respuesta;
	}
	
	public JSONArray getVentas(JSONObject params) throws SQLException {
		JSONArray respuesta = new JSONArray();
		List<Lugar> lugares = CommonQueries.getLugares(conexion);
		for (Lugar lugar : lugares) {
			List<Evento> eventos = getTodayVentasEventos(lugar);
			lugar.setEventos(eventos);
			Gson gson = new Gson();
			respuesta.put(new JSONArray(gson.toJson(lugares)).get(0));
		}
		return respuesta;
	}
	
	private List<Evento> getTodayVentasEventos(Lugar lugar) throws SQLException {
		String sql = getTodayVentasEventosSqlQuery(lugar);
		Statement stmt = conexion.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<Evento> eventos = new ArrayList<>();
		while(rs.next()) {
			eventos.add(new Evento(
		       		 rs.getInt(EventoTable.Cols.ID_EVENTO), 
		       		 rs.getInt(EventoTable.Cols.ID_LUGAR),
		       		 rs.getString(EventoTable.Cols.NOMBRE),
		       		 rs.getString(EventoTable.Cols.FECHA),
		       		 rs.getString(EventoTable.Cols.HORA),
		       		 rs.getDouble(EventoTable.Cols.TOTAL)
		   		 ));
		}
		
		return eventos;
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para obtener un apartado
	 * @param lugar 
	 * 
	 * @param params Parametros de la peticion
	 * 
	 * @return sql Consulta SQL
	 * 
	 */
	private static String getTodayVentasEventosSqlQuery(Lugar lugar) {
		String sql = "SELECT e.*, SUM(a." + ApartadoTable.Cols.PAGADO + ") as total"
				+ " FROM " + ApartadoTable.NAME + " a, " + EventoTable.NAME + " e"
				+ " WHERE DATE(a." + ApartadoTable.Cols.TIEMPO + ") = CURDATE()"
				+ " AND a." + ApartadoTable.Cols.ID_EVENTO + " = e." + EventoTable.Cols.ID_EVENTO
				+ " AND e." + EventoTable.Cols.ID_LUGAR + " = " + lugar.getIdLugar()
				+ " AND a." + ApartadoTable.Cols.PAGADO + " >= ("
					+ "SELECT SUM(ez." + EventoZonaTable.Cols.PRECIO + ")"
					+ " FROM " + EventoAsientoTable.NAME + " ea, " + EventoZonaTable.NAME + " ez"
					+ " WHERE ea." + EventoAsientoTable.Cols.ID_ZONA + " = ez." + EventoZonaTable.Cols.ID_ZONA
					+ " AND ea." + EventoAsientoTable.Cols.ID_EVENTO + " = ez." + EventoZonaTable.Cols.ID_EVENTO
					+ " AND ea." + EventoAsientoTable.Cols.ID_APARTADO + " = a." + ApartadoTable.Cols.ID_APARTADO
					+ " GROUP BY a." + ApartadoTable.Cols.ID_APARTADO 
				+ ")"
				+ " GROUP BY a." + ApartadoTable.Cols.ID_EVENTO;
		logger.debug(sql);
		return sql;
	}
}
