package com.itq.progradist.boletazo.controladores;

import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.modelos.Boleto;
import com.itq.progradist.boletazo.modelos.Evento;
import com.itq.progradist.boletazo.modelos.Zona;

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

	private JSONArray apartados;
	
	/**
	 * Inicializar un controlador con una conexión a la base de datos y
	 * datos de petición
	 * 
	 * @param conexion Conexión a la base de datos
	 * @param dataRequest Parámetros de la petición
	 */
	public ControladorApartado(Connection conexion, JSONObject dataRequest) {
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
			switch (params.getString("metodo")) {
			case "post":
				logger.info("Guardando apartado");
				Thread.sleep(1000);
				respuesta = this.procesoApartado(params);
				logger.info("Apartado guardado");
				return respuesta;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get("method"));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acción:" + e.getMessage());
			return new JSONObject().put("message", e.getMessage());
		} catch (InterruptedException e) {
			logger.error("Error en el sleep: " + e.getMessage());
			e.printStackTrace();
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
	 * @return respuesta Datos del apartado recien guardado. Si no se guardo solo contiene el mensaje de error
	 */
	private JSONObject procesoApartado(JSONObject params) {
		synchronized (ControladorApartado.class) {
			logger.info("Iniciando consulta en la base de datos");
			
			try {
				JSONObject respuesta = new JSONObject();
				
				checkNumBoletos(params);
				
				Statement stmt = this.conexion.createStatement();
				
				logger.info("Comprobando disponibilidad de los boletos");
				
				comprobarDispBoletos(params);
				
				logger.info("Comprobación de dispobilidad exitosa");
				
				logger.info("Guardando informacion del apartado");
				guardarApartados(params);
				logger.info("Informacion del apartado guardada");
				
				logger.info("Actualizando informacion de los asientos apartados");
				
				JSONArray boletos = params.getJSONArray("num_boletos");
				String sql;
				for (int i = 0; i < boletos.length(); i++) {
					JSONObject boleto = boletos.getJSONObject(i);
					int idApartado = apartados.getJSONObject(apartados.length() - 1).getInt("idApartado");
					
					sql = getGuardarApartadoAsientosSqlQuery(boleto, idApartado);
					stmt.executeUpdate(sql);
				}
				
				logger.info("Informacion de los asientos apartados realizada");
				
				respuesta.put("respuesta", "Registrado");
				respuesta.put("evento_id", params.getInt("evento_id"));
				respuesta.put("num_boletos", params.getJSONArray("num_boletos").length());
				respuesta.put("zona_id", params.getInt("zona_id"));
				respuesta.put("apartados", apartados);
				
				return respuesta;
				
			} catch (FaltanParametrosException e) {
				
				logger.error(e.getMessage());
				e.printStackTrace();
				return new JSONObject().put("message", e.getMessage());
				
			} catch (SQLException e) {
				
				logger.error("Error al consultar la base de datos: " + e.getMessage());
				e.printStackTrace();
				return new JSONObject().put("message", e.getMessage());
				
			} catch (BoletosExcedidosException e) {
				
				logger.error(e.getMessage());
				e.printStackTrace();
				return new JSONObject().put("message", e.getMessage());
				
			} catch (JSONException e) {
				
				logger.error(e.getMessage());
				e.printStackTrace();
				return new JSONObject().put("message", e.getMessage());
				
			} catch (AsientoOcupadoException e) {
				
				logger.error(e.getMessage());
				e.printStackTrace();
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
	private synchronized void guardarApartados(JSONObject params) throws FaltanParametrosException, SQLException {
		this.apartados = new JSONArray();
		String sql = getGuardarApartadoSqlQuery(params);
		Statement stmt = this.conexion.createStatement();
		stmt.executeUpdate(sql);
		ResultSet rs = stmt.executeQuery(getApartadoSqlQuery(params));
		while (rs.next()) {
			Apartado apartado = new Apartado(
	        		 rs.getInt("idApartado"),
	        		 rs.getInt("idUsuario"), 
	        		 rs.getInt("idEvento"),
	        		 rs.getDouble("pagado"),
	        		 rs.getString("tiempo")
        		 );
	         Gson gson = new Gson();
	         JSONObject apartadoJson = new JSONObject(gson.toJson(apartado));
	         apartados.put(apartadoJson);
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
		if(!params.has("num_boletos")) {
			throw new FaltanParametrosException("Falta el asiento_id en los parámetros de la petición");
		}
		
		JSONArray boletos = params.getJSONArray("num_boletos");
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
	 * @return true Devuelve true si el boleto esta disponible para apartar de otra manera lanza una exception
	 * 
	 * @throws SQLException
	 * @throws JSONException
	 * @throws AsientoOcupadoException
	 */
	private boolean checarEstaDisponible(JSONObject boleto) throws SQLException, JSONException, AsientoOcupadoException {
		String sql = "SELECT * FROM eventosasientos "
				+ " WHERE idApartado IS NULL "
				+ " AND idAsiento = " + boleto.getInt("asiento_id");
		Statement stmt = this.conexion.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.first()) {
			return true;
		}
		throw new AsientoOcupadoException("El asiento con id " + boleto.getInt("asiento_id") + " está ocupado");		
	}

	/**
	 * Devuelve la consulta SQL que sirve para actualizar
	 * el usuario que ha apartado el asiento del evento
	 * 
	 * @param boleto El boleto que contiene el asiento que apartara el cliente
	 * @param idApartado El id del apartado del cliente
	 * @return sql Consulta SQL
	 * @throws FaltanParametrosException
	 */
	private String getGuardarApartadoAsientosSqlQuery(JSONObject boleto, int idApartado) throws FaltanParametrosException {
		if(!boleto.has("asiento_id")) {
			throw new FaltanParametrosException("Falta el asiento_id en los parámetros de la petición");
		}
		String sql = "UPDATE eventosasientos SET idApartado = " + idApartado 
				+ " WHERE idAsiento = " + boleto.getInt("asiento_id");
		return sql;
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	private JSONArray hacerPago(JSONObject params) {
		return null;
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para insertar
	 * el apartado que solicita el cliente
	 * 
	 * @param params Parametros de la peticion
	 * @return sql Consulta SQL
	 * @throws FaltanParametrosException
	 */
	private String getGuardarApartadoSqlQuery(JSONObject params) throws FaltanParametrosException {
		if(!params.has("evento_id")) {
			throw new FaltanParametrosException("Falta el evento_id en los parámetros de la petición");
		}
		if(!params.has("usuario_id")) {
			throw new FaltanParametrosException("Falta el usuario_id en los parámetros de la petición");
		}
		String sql = "INSERT INTO Apartados (idEvento, idUsuario) VALUES ("
				+ params.getInt("evento_id") + ", "
				+ params.getInt("usuario_id") + ")";
		return sql;
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para obtener 
	 * el ultimo realizado por el cliente de un evento en especifico
	 * 
	 * @param params Parametros de la peticion
	 * @return sql Consulta SQL
	 * @throws FaltanParametrosException
	 */
	private String getApartadoSqlQuery(JSONObject params) throws FaltanParametrosException {
		if(!params.has("evento_id")) {
			throw new FaltanParametrosException("Falta el evento_id en los parámetros de la petición");
		}
		if(!params.has("usuario_id")) {
			throw new FaltanParametrosException("Falta el usuario_id en los parámetros de la petición");
		}
		String sql = "SELECT * FROM Apartados WHERE idEvento = "
				+ params.getInt("evento_id") + " AND idUsuario = "
				+ params.getInt("usuario_id") + " "
				+ "ORDER BY tiempo DESC "
				+ "LIMIT 0,1";
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

		public BoletosExcedidosException() {
			super("Número de boletos excedido: Máximo 4 boletos por apartado");
		}
	}
	
	/**
	 * Checa si el numero de boletos de la peticion excede o no
	 * el limite de boletos por apartado
	 * 
	 * @param params Parametros de la peticion de apartado
	 * @return devuelve true si el numero de boletos no excede el limite
	 * @throws FaltanParametrosException
	 * @throws BoletosExcedidosException
	 */
	private boolean checkNumBoletos(JSONObject params) throws FaltanParametrosException, BoletosExcedidosException {
		if(!params.has("num_boletos")) {
			throw new FaltanParametrosException("Faltan num_boletos en los parámetros de la petición");
		}
		if(params.getJSONArray("num_boletos").length() > 4) {
			throw new BoletosExcedidosException();
		}
		return true;
	}
}
