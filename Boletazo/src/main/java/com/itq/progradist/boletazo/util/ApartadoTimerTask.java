package com.itq.progradist.boletazo.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itq.progradist.boletazo.BoletazoServer;
import com.itq.progradist.boletazo.database.CommonQueries;
import com.itq.progradist.boletazo.database.DatabaseHandler;
import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoAsientoTable;
import com.itq.progradist.boletazo.modelos.Apartado;

public class ApartadoTimerTask extends TimerTask {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ApartadoTimerTask.class);
	
	private static final int DELAY = 1000;
	
	private static final int PERIOD = BoletazoServer.RESERVED_TIME;
	
	private int seconds;
	
	private Apartado apartado;
	
	private Connection connection;
	
	/**
	 * 
	 * @param idApartado
	 */
	public ApartadoTimerTask(Apartado apartado) {
		this.apartado = apartado;
		this.connection = DatabaseHandler.getConnection();
		this.seconds = 0;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		seconds ++;
		try {
			logger.debug("Segundos transcurridos: " + seconds * DELAY);
			Statement stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery(getCantidadPagadaSqlQuery());
			rs.next();
			double pagado = rs.getDouble(ApartadoTable.Cols.PAGADO);
			
			double importe = CommonQueries.calculateImporteOf(connection, apartado);
			
			if (seconds * DELAY >= PERIOD) {
				logger.info("El tiempo del apartado " + this.apartado.getIdApartado() + " ha caducado");
				logger.info("Total pagado: " + pagado + " del apartado " + apartado.getIdApartado() + " al momento de la comprobación");
				logger.info("Importe: " + importe + " del apartado " + apartado.getIdApartado() + " al momento de la comprobación");
				
				if(pagado < importe) {
					logger.debug("Iniciando borrado del apartado " + apartado.getIdApartado());
					resetAsientos();
					deleteApartado();
					logger.info("El apartado con número " + apartado.getIdApartado() + " ha sido caducado");
				}
				DatabaseHandler.cerrarConexion(connection);
				cancel();
			} else if(pagado >= importe) {
				DatabaseHandler.cerrarConexion(connection);
				cancel();
			}
			
			/*if (seconds * DELAY >= PERIOD) {
				logger.info("Iniciando comprobación de pago del apartado " + apartado.getIdApartado());
				
				stmt = connection.createStatement();
				
				rs = stmt.executeQuery(getCantidadPagadaSqlQuery());
				rs.next();
				pagado = rs.getDouble(ApartadoTable.Cols.PAGADO);
				
				importe = CommonQueries.calculateImporteOf(connection, apartado);
				
				logger.info("Total pagado: " + pagado + " del apartado " + apartado.getIdApartado() + " al momento de la comprobación");
				logger.info("Importe: " + importe + " del apartado " + apartado.getIdApartado() + " al momento de la comprobación");
				if(pagado < importe) {
					logger.debug("Iniciando borrado del apartado " + apartado.getIdApartado());
					resetAsientos();
					deleteApartado();
					logger.info("El apartado con número " + apartado.getIdApartado() + " ha sido caducado");
				}
				
				DatabaseHandler.cerrarConexion(connection);
				
				cancel();
			}*/
		} catch (SQLException e) {
			logger.error("Error al consultar la base de datos: " + e.getMessage());
			logger.catching(e);
		}
	}
	
	/**
	 * 
	 */
	private void resetAsientos() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(getResetAsientoSqlQuery());
		} catch (SQLException e) {
			logger.error("Error al resetear los asientos: " + e.getMessage());
			logger.catching(e);
		}
	}
	
	/**
	 * 
	 */
	private void deleteApartado() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(getBorrarApartadoSqlQuery());
		} catch (SQLException e) {
			logger.error("Error al borrar el apartado: " + e.getMessage());
			logger.catching(e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String getCantidadPagadaSqlQuery() {
		return "SELECT a.pagado"
				+ " FROM " + ApartadoTable.NAME + " a"
				+ " WHERE a." + ApartadoTable.Cols.ID_APARTADO + " = " + apartado.getIdApartado() 
				+ " LIMIT 0,1";
	}
	
	/**
	 * 
	 * @return
	 */
	private String getResetAsientoSqlQuery() {
		return "UPDATE " + EventoAsientoTable.NAME 
				+ " SET " + EventoAsientoTable.Cols.ID_APARTADO + " = NULL"
				+ " WHERE " + EventoAsientoTable.Cols.ID_APARTADO + " = " + apartado.getIdApartado();
	}
	
	/**
	 * 
	 * @return
	 */
	private String getBorrarApartadoSqlQuery() {
		return "DELETE FROM " + ApartadoTable.NAME
				+ " WHERE " + ApartadoTable.Cols.ID_APARTADO + " = " + apartado.getIdApartado();
	}
	
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(this, 0, DELAY);
	}

}
