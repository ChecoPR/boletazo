package com.itq.progradist.boletazo.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.itq.progradist.boletazo.database.DatabaseSchema.EventoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.LugarTable;
import com.itq.progradist.boletazo.modelos.Evento;
import com.itq.progradist.boletazo.modelos.Lugar;

public class DatabaseSeeder {
	
	public static void seed() throws SQLException {
		Connection connection = DatabaseHandler.getConnection();
		
		ArrayList<Lugar> lugares = new ArrayList<Lugar>();
		lugares.add(new Lugar("Estadio Corregidora", "Querétaro"));
		
		Statement stmt;
		for (Lugar lugar : lugares) {
			stmt = connection.createStatement();
			stmt.executeUpdate(getLugarSqlquery(lugar));
		}
		
		ArrayList<Evento> eventos = new ArrayList<Evento>();
		for (Lugar lugar : lugares) {
			eventos.add(new Evento(lugar.getIdLugar(), "José José", "2019-10-26", "10:00:00"));
		}
		
		for (Evento evento : eventos) {
			stmt = connection.createStatement();
			stmt.executeUpdate(getEventosSqlQuery(evento));
		}
	}
	
	private static String getLugarSqlquery(Lugar lugar) {
		return "INSERT INTO " + LugarTable.NAME
				+ " (" + LugarTable.Cols.NOMBRE 
				+ ", " + LugarTable.Cols.ESTADO 
				+ ")" 
				+ " VALUES "
				+ "(" + lugar.getNombre() 
				+ ", " + lugar.getEstado() 
				+ ")";
	}
	
	private static String getEventosSqlQuery(Evento evento) {
		return "INSERT INTO " + EventoTable.NAME
				+ " (" + EventoTable.Cols.NOMBRE 
				+ ", " + EventoTable.Cols.HORA
				+ ", " + EventoTable.Cols.ID_LUGAR
				+ ", " + EventoTable.Cols.FECHA
				+ ")" 
				+ " VALUES "
				+ "(" + evento.getNombre()
				+ ", " + evento.getHora()
				+ ", " + evento.getIdLugar()
				+ ", " + evento.getFecha()
				+ ")";
	}
	
	public static void main(String[] args) {
		try {
			seed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
