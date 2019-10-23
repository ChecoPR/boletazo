package com.itq.progradist.boletazo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BoletazoDatabaseHandler {
	
	/**
	 * logger de la clase Database
	 */
	private static final Logger logger = LogManager.getLogger(BoletazoDatabaseHandler.class);
	
	private static final String HOST = "192.168.1.2";
	
	private static final String PORT = "3306";
	
	private static final String DATABASE = "boletazo";
	
	private static final String USER = "boletazo";
	
	private static final String PASSWORD = "password";
	
	private static final String CONNECTION_PARAMS = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	/**
	 * Devuelve una conexion a la base de datos
	 * 
	 * @return conexion Conexion a la base de datos
	 */
	public static Connection getConnection() {
		Connection conexion;
		String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + CONNECTION_PARAMS;
		try {
			conexion = DriverManager.getConnection(url,USER,PASSWORD);
			logger.info("Conectado exitosamente a la base de datos " + url);
			return conexion;
		} catch (SQLException e) {
			logger.error("Error al conectar con la base de datos " + url + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
