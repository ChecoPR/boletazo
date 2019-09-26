package com.itq.progradist.boletazo.modelos;

public class Lugar {
	/**
	 * Propiedades de un lugar
	 */
	private int idLugar;
	private String nombre;
	
	public Lugar(int idLugar, String nombre) {
		super();
		this.idLugar = idLugar;
		this.nombre = nombre;
	}
	
	public int getIdLugar() {
		return idLugar;
	}
	public void setIdLugar(int idLugar) {
		this.idLugar = idLugar;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}
