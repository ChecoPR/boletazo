package com.itq.progradist.boletazo.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoAsientoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoZonaTable;
import com.itq.progradist.boletazo.exceptions.ApartadoNotFound;
import com.itq.progradist.boletazo.modelos.Apartado;

public class CommonQueries {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(CommonQueries.class);

	public static Apartado getApartadoById(Connection connection, int idApartado) throws SQLException, ApartadoNotFound {
		String sql = getApartadoSqlQuery(idApartado);
		Statement stmt = connection.createStatement();
		logger.info("Ejecutando consulta");
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
}
