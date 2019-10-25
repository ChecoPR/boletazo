package com.itq.progradist.boletazo.controladores;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.itq.progradist.boletazo.ParamNames.Metodo;
import com.itq.progradist.boletazo.ParamNames.Recurso.Pago;
import com.itq.progradist.boletazo.database.CommonQueries;
import com.itq.progradist.boletazo.exceptions.ApartadoNotFound;
import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.MetodoPagoTable;
import com.itq.progradist.boletazo.exceptions.ParamMetodoNotFoundException;
import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.modelos.MetodoPago;

/**
 * Realiza los procesos que tienen que ver con el tipo de recurso "pago".
 * Se inicia con una conexion a la base de datos y los datos de la peticion.
 * 
 * @author Equipo 5
 *
 */
public class ControladorPago {
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ControladorPago.class);
	
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
	public ControladorPago(Connection conexion) {
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
			case Pago.Metodo.POST:
				logger.info("Realizando pago");
				respuesta.put("data", this.hacerPago(params));
				logger.info("Pago realizado");
				return respuesta;
			default:
				throw new IllegalArgumentException("Unexpected value: " + params.get(Metodo.KEY_NAME));
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error procesando la acci�n" + e.getMessage());
			logger.catching(e);
			respuesta.put("message", e.getMessage());
		}
		return respuesta;
	}

	/**
	 * Realiza el proceso para realizar el pago 
	 * de un apartado. El apartado debe estar especificado en 
	 * los parametros de la petici�n.
	 * 
	 * @param params Par�metros de la petici�n para realizar pago
	 * @return respuesta Devuelve la informaci�n del apartado pagado o un mensaje de error
	 */
	private JSONObject hacerPago(JSONObject params) {
		logger.info("Iniciando proceso de pago");
		JSONObject respuesta = new JSONObject();
		try {
			if (!params.has(Pago.Values.METODO_PAGO)) {
				throw new ParamIdMetodoPagoNotFound();
			}
			if (!params.has(Pago.Values.ID_APARTADO)) {
				throw new ParamIdApartadoNotFound();
			}
			
			logger.info("Consultando saldo en el m�todo de pago");
			int idMetodoPago = params.getInt(Pago.Values.METODO_PAGO);
			MetodoPago metodoPago = obtenerMetodoPago(idMetodoPago);
			
			int idApartado = params.getInt(Pago.Values.ID_APARTADO);
			Apartado apartado = CommonQueries.getApartadoById(conexion, idApartado);
			double importe = CommonQueries.calculateImporteOf(conexion, apartado);
			
			if(metodoPago.getSaldo() < importe) {
				throw new SaldoInsuficienteException(idMetodoPago, idApartado);
			}
			
			actualizarSaldo(idMetodoPago, importe);
			
			actualizarCantidadPagada(idApartado, importe);
			
			respuesta.put("respuesta", "Pagado");
			respuesta.put("apartado_id", idApartado);
			respuesta.put("metodo_pago_id", idMetodoPago);
			
		} catch (ParamIdApartadoNotFound | ParamIdMetodoPagoNotFound | MetodoPagoNotFound e) {
			logger.error(e.getMessage());
			logger.catching(e);
			respuesta.put("respuesta", "Error");
			respuesta.put("message", e.getMessage());
		} catch (JSONException e) {
			logger.error("Error en el JSON: " + e.getMessage());
			logger.catching(e);
			respuesta.put("respuesta", "Error");
			respuesta.put("message", e.getMessage());
		} catch (SaldoInsuficienteException e) {
			logger.error(e.getMessage());
			logger.catching(e);
			respuesta.put("respuesta", "No Pagado");
			respuesta.put("message", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al consultar la base de datos: " + e.getMessage());
			logger.catching(e);
			respuesta.put("respuesta", "Error");
			respuesta.put("message", "Error al consultar la base de datos");
		} catch (ApartadoNotFound e) {
			logger.error(e.getMessage());
			logger.catching(e);
			respuesta.put("respuesta", "Error");
			respuesta.put("message", e.getMessage());
		}
		
		return respuesta;
	}

	/**
	 * Actualiza la cantidad pagada del apartado
	 * 
	 * @param idApartado Apartado que se est� pagando
	 * @throws SQLException 
	 */
	private void actualizarCantidadPagada(int idApartado, double pagado) throws SQLException {
		String sql = getActualizarCatidadPagadaSqlQuery(pagado, idApartado);
		Statement stmt = this.conexion.createStatement();
		logger.info("Ejecutando consulta");
		stmt.executeUpdate(sql);
	}

	
	/**
	 * Actualiza la cantidad pagada del saldo del m�todo de pago
	 * con el que se est� pagando
	 * 
	 * @param idMetodoPago M�todo de pago con el que se est� pagando
	 * @throws SQLException 
	 */
	private void actualizarSaldo(int idMetodoPago, double pagado) throws SQLException {
		String sql = getActualizarSaldoSqlQuery(pagado, idMetodoPago);
		Statement stmt = this.conexion.createStatement();
		logger.info("Ejecutando consulta");
		stmt.executeUpdate(sql);
	}

	/**
	 * Devuelve un objeto de tipo m�todo de pago por id
	 * 
	 * @param idMetodoPago ID del m�todo de pago del que se solicita la informaci�n
	 * 
	 * @return metodoPago M�todo de pago que se solicit�
	 *  
	 * @throws MetodoPagoNotFound 
	 * @throws SQLException 
	 */
	private MetodoPago obtenerMetodoPago(int idMetodoPago) throws MetodoPagoNotFound, SQLException {
		String sql = getMetodoPagoSQLQuery(idMetodoPago);
		
		Statement stmt = this.conexion.createStatement();
		logger.info("Ejecutando consulta");
		ResultSet rs = stmt.executeQuery(sql);
		
		if(!rs.next()) {
			throw new MetodoPagoNotFound(idMetodoPago);
		}
		
		MetodoPago metodoPago = new MetodoPago(
				rs.getInt(MetodoPagoTable.Cols.ID_METODO_PAGO), 
				rs.getInt(MetodoPagoTable.Cols.ID_USUARIO),
				rs.getDouble(MetodoPagoTable.Cols.SALDO)
			);
		
		return metodoPago;
	}

	/**
	 * Devuelve una consulta para obtener la informaci�n del m�todo de pago
	 * 
	 * @param idMetodoPago M�todo de pago consultado
	 * @return sql Consulta SQL
	 */
	private String getMetodoPagoSQLQuery(int idMetodoPago) {
		return "SELECT mp.*"
				+ " FROM " + MetodoPagoTable.NAME + " mp"
				+ " WHERE mp." + MetodoPagoTable.Cols.ID_METODO_PAGO
				+ " LIMIT 0,1";
	}
	
	
	/**
	 * Devuelve la consulta SQL para actualizar el saldo del m�todo de pago.
	 * 
	 * @param pagado Catidad pagada con el m�todo de pago
	 * @param idMetodoPago M�todo de pago utilizado para pagar
	 * @return sql Consulta SQL
	 */
	private String getActualizarSaldoSqlQuery(double pagado, int idMetodoPago) {
		return "UPDATE " + MetodoPagoTable.NAME
				+ " SET " + MetodoPagoTable.Cols.SALDO + " = " + MetodoPagoTable.Cols.SALDO + " - " + pagado
				+ " WHERE " + MetodoPagoTable.Cols.ID_METODO_PAGO + " = " + idMetodoPago;
	}
	
	/**
	 * Devuelve la consulta SQL para actualizar la cantidad pagada del apartado. 
	 * 
	 * @param pagado Cantidad pagada del apartado
	 * @param idApartado Apartado a pagar
	 * @return sql Consulta SQL
	 */
	private String getActualizarCatidadPagadaSqlQuery(double pagado, int idApartado) {
		return "UPDATE " + ApartadoTable.NAME
				+ " SET " + ApartadoTable.Cols.PAGADO + " = " + ApartadoTable.Cols.PAGADO + " + " + pagado 
				+ " WHERE " + ApartadoTable.Cols.ID_APARTADO + " = " + idApartado;
	}
	
	/**
	 * Se lanza cuando el m�todo de pago a consultar 
	 * no existe en la base de datos
	 * 
	 * @author Equipo 5
	 *
	 */
	public class MetodoPagoNotFound extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Inicializa la exception con un mensaje de error predeterminado.
		 */
		public MetodoPagoNotFound(int idMetodoPago) {
			super("No se encontro el m�todo de pago: " + idMetodoPago + " en la base de datos");
		}
	}
	
	/**
	 * Se lanza cuando no se encuentra el par�metro 
	 * con el id del m�todo de pago
	 * 
	 * @author Equipo 5
	 *
	 */
	public class ParamIdMetodoPagoNotFound extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Inicializa la exception con un mensaje de error predeterminado.
		 */
		public ParamIdMetodoPagoNotFound() {
			super("Falta el " + Pago.Values.METODO_PAGO + " en la petici�n");
		}
	}
	
	/**
	 * Se lanza cuando no se encuentra el par�metro 
	 * con el id del apartado a pagar
	 * 
	 * @author Equipo 5
	 *
	 */
	public class ParamIdApartadoNotFound extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Inicializa la exception con un mensaje de error predeterminado.
		 */
		public ParamIdApartadoNotFound() {
			super("Falta el " + Pago.Values.METODO_PAGO + " en la petici�n");
		}
	}
	
	/**
	 * Se lanza cuando el saldo del m�todo de pago 
	 * es insufiente para pagar el apartado solicitado
	 * 
	 * @author Equipo 5
	 *
	 */
	public class SaldoInsuficienteException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Inicializa la exception con un mensaje de error predeterminado.
		 */
		public SaldoInsuficienteException(int idMetodoPago, int idApartado) {
			super("El saldo del m�todo de pago " + idMetodoPago + " es insuficiente para pagar el apartado " + idApartado);
		}
	}
}
