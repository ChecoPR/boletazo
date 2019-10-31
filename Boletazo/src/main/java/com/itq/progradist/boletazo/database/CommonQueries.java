package com.itq.progradist.boletazo.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoAsientoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoZonaTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.LugarTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.UsuarioTable;
import com.itq.progradist.boletazo.exceptions.ApartadoNotFound;
import com.itq.progradist.boletazo.exceptions.ModelNotFound;
import com.itq.progradist.boletazo.exceptions.UsuarioNotFound;
import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.modelos.Asiento;
import com.itq.progradist.boletazo.modelos.Evento;
import com.itq.progradist.boletazo.modelos.Lugar;
import com.itq.progradist.boletazo.modelos.Usuario;

public class CommonQueries {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(CommonQueries.class);
	
	public static String getDatabaseCurrentDate(Connection connection) throws SQLException {
		String sql = "SELECT CURDATE() as today";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		return rs.getString("today");
	}
	
	public static List<Lugar> getLugares(Connection connection) throws SQLException {
		String sql = "SELECT * FROM " + LugarTable.NAME;
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		List<Lugar> lugares = new ArrayList<Lugar>();
		while(rs.next()) {
			lugares.add(new Lugar(
					rs.getInt(LugarTable.Cols.ID_LUGAR), 
					rs.getString(LugarTable.Cols.NOMBRE), 
					rs.getString(LugarTable.Cols.ESTADO))
				);
		}
		return lugares;
	}

	public static Apartado getApartadoById(Connection connection, int idApartado) throws SQLException, ApartadoNotFound {
		String sql = getApartadoSqlQuery(idApartado);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(!rs.next()) {
			throw new ApartadoNotFound(idApartado);
		}
		return new Apartado(
				rs.getInt(ApartadoTable.Cols.ID_APARTADO), 
				rs.getInt(ApartadoTable.Cols.ID_USUARIO), 
				rs.getInt(ApartadoTable.Cols.ID_EVENTO), 
				rs.getDouble(ApartadoTable.Cols.PAGADO), 
				rs.getString(ApartadoTable.Cols.TIEMPO)
			);
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para obtener un apartado
	 * 
	 * @param params Parametros de la peticion
	 * 
	 * @return sql Consulta SQL
	 * 
	 */
	private static String getApartadoSqlQuery(int idApartado) {
		String sql = "SELECT *"
				+ " FROM " + ApartadoTable.NAME 
				+ " WHERE " + ApartadoTable.Cols.ID_APARTADO + " = " + idApartado 
				+ " LIMIT 0,1";
		return sql;
	}
	
	public static List<Asiento> getAsientosOfApartado(Connection connection, int idApartado) throws SQLException, ModelNotFound {
		String sql = getAsientosOfApartadoSqlQuery(idApartado);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<Asiento> asientos = new ArrayList<>();
		boolean estado;
		while(rs.next()) {
			rs.getInt(EventoAsientoTable.Cols.ID_APARTADO);
			estado = rs.wasNull();
			asientos.add(new Asiento(
		       		 estado, 
		       		 rs.getInt(EventoAsientoTable.Cols.ID_ASIENTO),
		       		 rs.getInt(EventoAsientoTable.Cols.ID_ZONA),
		       		 rs.getInt(EventoAsientoTable.Cols.ID_EVENTO)
		   		 ));
		}
		
		return asientos;
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para obtener un apartado
	 * 
	 * @param params Parametros de la peticion
	 * 
	 * @return sql Consulta SQL
	 * 
	 */
	private static String getAsientosOfApartadoSqlQuery(int idApartado) {
		String sql = "SELECT *"
				+ " FROM " + EventoAsientoTable.NAME 
				+ " WHERE " + ApartadoTable.Cols.ID_APARTADO + " = " + idApartado;
		return sql;
	}
	
	/**
	 * Devuelve el importe a pagar de un apartado
	 * 
	 * @param idApartado Apartado del que se quiere obtener el importe
	 * @throws SQLException 
	 */
	public static double calculateImporteOf(Connection connection, Apartado apartado) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(getImporteApartadoSqlQuery(apartado));
		rs.next();
		double importe = rs.getDouble(ApartadoTable.Cols.IMPORTE);
		return importe;
	}
	
	/**
	 * Devuelve la consulta SQL para consultar el importe a pagar del apartado
	 * 
	 * @param idApartado ID del apartado del que se quiere obtener el importe
	 * @return sql Consulta SQL
	 */
	private static String getImporteApartadoSqlQuery(Apartado apartado) {
		return "SELECT SUM(ez." + EventoZonaTable.Cols.PRECIO + ") as " + ApartadoTable.Cols.IMPORTE
				+ " FROM " + EventoAsientoTable.NAME + " ea, " + EventoZonaTable.NAME + " ez"
				+ " WHERE ea." + EventoAsientoTable.Cols.ID_ZONA + " = ez." + EventoZonaTable.Cols.ID_ZONA
				+ " AND ea." + EventoAsientoTable.Cols.ID_EVENTO + " = ez." + EventoZonaTable.Cols.ID_EVENTO
				+ " AND ea." + EventoAsientoTable.Cols.ID_APARTADO + " = " + apartado.getIdApartado()
				+ " AND ea." + EventoAsientoTable.Cols.ID_EVENTO + " = " + apartado.getIdEvento()
				+ " GROUP BY ea." + EventoAsientoTable.Cols.ID_APARTADO;
	}
	
	public static Usuario getUsuarioById(Connection connection, int idUsuario) throws SQLException, UsuarioNotFound {
		String sql = getUserSqlQuery(idUsuario);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(!rs.next()) {
			throw new UsuarioNotFound(idUsuario);
		}
		return new Usuario(
				rs.getInt(UsuarioTable.Cols.ID_USUARIO), 
				rs.getString(UsuarioTable.Cols.NOMBRE),
				rs.getString(UsuarioTable.Cols.EMAIL)
			);
	}

	private static String getUserSqlQuery(int idUser) {
		return "SELECT * "
				+ " FROM " + UsuarioTable.NAME 
				+ " WHERE " + UsuarioTable.Cols.ID_USUARIO + " = " + idUser;
	}
	
	public static Evento getEventoById(Connection connection, int idEvento) throws SQLException, ModelNotFound {
		String sql = getEventoSqlQuery(idEvento);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(!rs.next()) {
			throw new ModelNotFound(EventoTable.NAME, idEvento);
		}
		return new Evento(
       		 rs.getInt(EventoTable.Cols.ID_EVENTO), 
       		 rs.getInt(EventoTable.Cols.ID_LUGAR), 
       		 rs.getString(EventoTable.Cols.NOMBRE),
       		 rs.getString(EventoTable.Cols.FECHA),
       		 rs.getString(EventoTable.Cols.HORA)
   		 );
	}

	private static String getEventoSqlQuery(int idEvento) {
		return "SELECT * "
				+ " FROM " + EventoTable.NAME 
				+ " WHERE " + EventoTable.Cols.ID_EVENTO + " = " + idEvento;
	}
	
	public static Lugar getLugarById(Connection connection, int idLugar) throws SQLException, ModelNotFound {
		String sql = getLugarSqlQuery(idLugar);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(!rs.next()) {
			throw new ModelNotFound(EventoTable.NAME, idLugar);
		}
		return new Lugar(
				rs.getInt(LugarTable.Cols.ID_LUGAR), 
				rs.getString(LugarTable.Cols.NOMBRE), 
				rs.getString(LugarTable.Cols.ESTADO)
			);
	}

	private static String getLugarSqlQuery(int idLugar) {
		return "SELECT * "
				+ " FROM " + LugarTable.NAME 
				+ " WHERE " + LugarTable.Cols.ID_LUGAR + " = " + idLugar;
	}
}
