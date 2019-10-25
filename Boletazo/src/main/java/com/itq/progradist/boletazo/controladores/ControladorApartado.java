package com.itq.progradist.boletazo.controladores;

import java.sql.*;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.ParamNames.Metodo;
import com.itq.progradist.boletazo.ParamNames.Recurso.Apartado.Boletos;
import com.itq.progradist.boletazo.database.CommonQueries;
import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoAsientoTable;
import com.itq.progradist.boletazo.exceptions.ParamMetodoNotFoundException;
import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.util.ApartadoTimerTask;

import static com.itq.progradist.boletazo.ParamNames.*;

/**
 * Realiza los procesos que tienen que ver con el tipo de recurso "apartado".
 * Se inicia con una conexion a la base de datos y los datos de la peticion.
 * 
 * @author Equipo 5
 *
 */
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
	 * Inicializar un controlador con una conexi�n a la base de datos y
	 * datos de petici�n
	 * 
	 * @param conexion Conexi�n a la base de datos
	 * @param dataRequest Par�metros de la petici�n
	 */
	public ControladorApartado(Connection conexion) {
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
			case Metodo.Values.POST:
				logger.info("Guardando apartado");
				respuesta = this.procesoApartado(params);
				logger.info("Apartado guardado");
				return respuesta;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Metodo.KEY_NAME));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acci�n:" + e.getMessage());
			return new JSONObject().put("message", e.getMessage());
		}
	}

	/**
	 * 
	 * @param idApartado
	 * @return
	 */
//	private ArrayList<Boleto> getBoletos(int idApartado) {
//		return null;
//	}
	
	/**
	 * Realiza el proceso para guardar un apartado
	 * 
	 * @param params Parametros de la peticion para guardar el apartado
	 * 
	 * @return respuesta Datos del apartado recien guardado. Si no se guardo solo contiene el mensaje de error
	 */
	private JSONObject procesoApartado(JSONObject params) {
		synchronized (ControladorApartado.class) {
			logger.info("Iniciando consulta en la base de datos");
			
			try {
				JSONObject respuesta = new JSONObject();
				
				checkNumBoletos(params);
				
				logger.info("Comprobando disponibilidad de los boletos");
				
				comprobarDispBoletos(params);
				
				logger.info("Comprobaci�n de dispobilidad exitosa");
				
				logger.info("Guardando informacion del apartado");
				
				Apartado apartadoActualizado = guardarApartado(params);
				
				logger.info("Informacion del apartado guardada");
				
				logger.info("Actualizando informacion de los asientos apartados");
				
				actualizarAsientos(params, apartadoActualizado);
				
				double importe = CommonQueries.calculateImporteOf(conexion, apartadoActualizado);
				apartadoActualizado.setImporte(importe);
				
				logger.info("Informacion de los asientos apartados realizada");
				
				JSONObject apartadoJson = new JSONObject(new Gson().toJson(apartadoActualizado));
				
				respuesta.put("respuesta", "Registrado");
				respuesta.put("evento_id", params.getInt(Recurso.Apartado.Values.ID_EVENTO));
				respuesta.put("num_boletos", params.getJSONArray(Boletos.KEY).length());
				respuesta.put("zona_id", params.getInt(Recurso.Apartado.Values.ID_ZONA));
				respuesta.put("apartados", apartadoJson);
				
				logger.info("Iniciando cuenta atr�s para la caducidad del apartado");
				ApartadoTimerTask task = new ApartadoTimerTask(apartadoActualizado);
				task.schedule();
				
				return respuesta;
				
			} catch (FaltanParametrosException e) {
				
				logger.error(e.getMessage());
				logger.catching(e);
				return new JSONObject().put("message", e.getMessage());
				
			} catch (SQLException e) {
				
				logger.error("Error al consultar la base de datos: " + e.getMessage());
				logger.catching(e);
				return new JSONObject().put("message", "Error al consultar la base de datos");
				
			} catch (BoletosExcedidosException e) {
				
				logger.error(e.getMessage());
				logger.catching(e);
				return new JSONObject().put("message", e.getMessage());
				
			} catch (JSONException e) {
				
				logger.error(e.getMessage());
				logger.catching(e);
				return new JSONObject().put("message", e.getMessage());
				
			} catch (AsientoOcupadoException e) {
				
				logger.error(e.getMessage());
				logger.catching(e);
				return new JSONObject().put("message", e.getMessage());
				
			}
		}
	}
	
	/**
	 * Guardar el apartado en la base de datos una vez que se ha
	 * comprobado su validez
	 * 
	 * @param params Parametros de la peticion
	 * 
	 * @throws FaltanParametrosException
	 * @throws SQLException
	 */
	private Apartado guardarApartado(JSONObject params) throws FaltanParametrosException, SQLException {
		ArrayList<Apartado> apartados = new ArrayList<Apartado>();
		String sql = getGuardarApartadoSqlQuery(params);
		
		Statement stmt = this.conexion.createStatement();
		stmt.executeUpdate(sql);
		ResultSet rs = stmt.executeQuery(getApartadoSqlQuery(params));
		while (rs.next()) {
			Apartado apartado = new Apartado(
	        		 rs.getInt(ApartadoTable.Cols.ID_APARTADO),
	        		 rs.getInt(ApartadoTable.Cols.ID_USUARIO), 
	        		 rs.getInt(ApartadoTable.Cols.ID_EVENTO),
	        		 rs.getDouble(ApartadoTable.Cols.PAGADO),
	        		 rs.getString(ApartadoTable.Cols.TIEMPO)
        		 );
			
			apartados.add(apartado);
		}
		return apartados.get(apartados.size() - 1);
	}
	
	/**
	 * Actualiza la informaci�n de los asientos que se quieren apartar
	 * 
	 * @param params Parametros de la peticion
	 * @throws SQLException
	 * @throws FaltanParametrosException
	 */
	private void actualizarAsientos(JSONObject params, Apartado apartado) throws SQLException, FaltanParametrosException {
		Statement stmt = this.conexion.createStatement();
		JSONArray boletos = params.getJSONArray(Recurso.Apartado.Boletos.KEY);
		String sql;
		for (int i = 0; i < boletos.length(); i++) {
			JSONObject boleto = boletos.getJSONObject(i);
			int idApartado = apartado.getIdApartado();
			
			sql = getGuardarApartadoAsientosSqlQuery(boleto, idApartado);
			stmt.executeUpdate(sql);
		}
	}

	/**
	 * Realiza el proceso para checar si los boletos
	 * estan disponible. Si alguno no esta disponible para apartar
	 * entonces tira una exception AsientoOcupadoException.
	 * 
	 * @param boletos Boletos que quiere apartar el cliente
	 * 
	 * @throws JSONException
	 * @throws SQLException
	 * @throws AsientoOcupadoException
	 * @throws FaltanParametrosException 
	 */
	private void comprobarDispBoletos(JSONObject params) throws JSONException, SQLException, AsientoOcupadoException, FaltanParametrosException {
		if(!params.has(Boletos.KEY)) {
			throw new FaltanParametrosException("Falta el " + Boletos.KEY + " en los par�metros de la petici�n");
		}
		
		JSONArray boletos = params.getJSONArray(Boletos.KEY);
		for (int i = 0; i < boletos.length(); i++) {
			JSONObject boleto = boletos.getJSONObject(i);
			checarEstaDisponible(boleto);
		}
	}

	/**
	 * Realiza el proceso para checar si el boleto
	 * esta disponible para apartar
	 * 
	 * @param boleto Informacion del boleto
	 * 
	 * @return true Devuelve true si el boleto esta disponible para apartar de otra manera lanza una exception
	 * 
	 * @throws SQLException
	 * @throws JSONException
	 * @throws AsientoOcupadoException
	 */
	private boolean checarEstaDisponible(JSONObject boleto) throws SQLException, JSONException, AsientoOcupadoException {
		String sql = "SELECT * FROM " + EventoAsientoTable.NAME
				+ " WHERE " + EventoAsientoTable.Cols.ID_APARTADO + " IS NULL "
				+ " AND " + EventoAsientoTable.Cols.ID_ASIENTO + " = " + boleto.getInt(Recurso.Apartado.Boletos.ID_ASIENTO);
		Statement stmt = this.conexion.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.first()) {
			return true;
		}
		throw new AsientoOcupadoException("El asiento con id " + boleto.getInt(Recurso.Apartado.Boletos.ID_ASIENTO) + " est� ocupado");		
	}
	
	/**
	 * Checa si el numero de boletos de la peticion excede o no
	 * el limite de boletos por apartado
	 * 
	 * @param params Parametros de la peticion de apartado
	 * 
	 * @return devuelve true si el numero de boletos no excede el limite
	 * 
	 * @throws FaltanParametrosException
	 * @throws BoletosExcedidosException
	 */
	private boolean checkNumBoletos(JSONObject params) throws FaltanParametrosException, BoletosExcedidosException {
		if(!params.has(Recurso.Apartado.Boletos.KEY)) {
			throw new FaltanParametrosException("Faltan " + Recurso.Apartado.Boletos.KEY + " en los par�metros de la petici�n");
		}
		if(params.getJSONArray(Recurso.Apartado.Boletos.KEY).length() > 4) {
			throw new BoletosExcedidosException();
		}
		return true;
	}

	/**
	 * Devuelve la consulta SQL que sirve para actualizar
	 * el usuario que ha apartado el asiento del evento
	 * 
	 * @param boleto El boleto que contiene el asiento que apartara el cliente
	 * @param idApartado El id del apartado del cliente
	 * 
	 * @return sql Consulta SQL
	 * 
	 * @throws FaltanParametrosException
	 */
	private String getGuardarApartadoAsientosSqlQuery(JSONObject boleto, int idApartado) throws FaltanParametrosException {
		if(!boleto.has(Recurso.Apartado.Boletos.ID_ASIENTO)) {
			throw new FaltanParametrosException("Falta el " + Recurso.Apartado.Boletos.ID_ASIENTO + " en los par�metros de la petici�n");
		}
		String sql = "UPDATE " + EventoAsientoTable.NAME + " SET " + EventoAsientoTable.Cols.ID_APARTADO + " = " + idApartado 
				+ " WHERE " + EventoAsientoTable.Cols.ID_ASIENTO + " = " + boleto.getInt(Recurso.Apartado.Boletos.ID_ASIENTO);
		return sql;
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
//	private JSONArray hacerPago(JSONObject params) {
//		return null;
//	}
	
	/**
	 * Devuelve la consulta SQL que sirve para insertar
	 * el apartado que solicita el cliente
	 * 
	 * @param params Parametros de la peticion
	 * @return sql Consulta SQL
	 * @throws FaltanParametrosException
	 */
	private String getGuardarApartadoSqlQuery(JSONObject params) throws FaltanParametrosException {
		if(!params.has(Recurso.Apartado.Values.ID_EVENTO)) {
			throw new FaltanParametrosException("Falta el evento_id en los par�metros de la petici�n");
		}
		if(!params.has(Recurso.Apartado.Values.ID_USUARIO)) {
			throw new FaltanParametrosException("Falta el " + Recurso.Apartado.Values.ID_USUARIO + " en los par�metros de la petici�n");
		}
		String sql = "INSERT INTO " + ApartadoTable.NAME + " (" + ApartadoTable.Cols.ID_EVENTO + ", " + ApartadoTable.Cols.ID_USUARIO + ") VALUES ("
				+ params.getInt(Recurso.Apartado.Values.ID_EVENTO) + ", "
				+ params.getInt(Recurso.Apartado.Values.ID_USUARIO) + ")";
		return sql;
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para obtener 
	 * el ultimo realizado por el cliente de un evento en especifico
	 * 
	 * @param params Parametros de la peticion
	 * 
	 * @return sql Consulta SQL
	 * 
	 * @throws FaltanParametrosException
	 */
	private String getApartadoSqlQuery(JSONObject params) throws FaltanParametrosException {
		if(!params.has(Recurso.Apartado.Values.ID_EVENTO)) {
			throw new FaltanParametrosException("Falta el " + Recurso.Apartado.Values.ID_EVENTO + " en los par�metros de la petici�n");
		}
		if(!params.has(Recurso.Apartado.Values.ID_USUARIO)) {
			throw new FaltanParametrosException("Falta el " + Recurso.Apartado.Values.ID_USUARIO + " en los par�metros de la petici�n");
		}
		String sql = "SELECT *"
				+ " FROM " + ApartadoTable.NAME 
				+ " WHERE " + ApartadoTable.Cols.ID_EVENTO + " = " + params.getInt(Recurso.Apartado.Values.ID_EVENTO) 
				+ " AND " + ApartadoTable.Cols.ID_USUARIO + " = " + params.getInt(Recurso.Apartado.Values.ID_USUARIO)
				+ " ORDER BY " + ApartadoTable.Cols.TIEMPO + " DESC "
				+ " LIMIT 0,1";
		return sql;
	}
	
	/**
	 * Exception para cuando la peticion para
	 * obtener zonas de un evento no tiene el parametro id_evento
	 *
	 */
	private class FaltanParametrosException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Inicializa con un mensaje de error personalizado.
		 * 
		 * @param msg Mensaje de error.
		 */
		public FaltanParametrosException(String msg) {
			super(msg);
		}
	}
	
	/**
	 * Exception para cuando la peticion para
	 * obtener zonas de un evento no tiene el parametro id_evento
	 *
	 */
	private class AsientoOcupadoException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Inicializa con un mensaje de error personalizado.
		 * 
		 * @param msg Mensaje de error.
		 */
		public AsientoOcupadoException(String msg) {
			super(msg);
		}
	}
	
	/**
	 * Exception para cuando la peticion para
	 * obtener zonas de un evento no tiene el parametro id_evento
	 *
	 */
	private class BoletosExcedidosException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Inicializa con un mensaje de error personalizado.
		 * 
		 * @param msg Mensaje de error.
		 */
		public BoletosExcedidosException() {
			super("N�mero de boletos excedido: M�ximo 4 boletos por apartado");
		}
	}
}
