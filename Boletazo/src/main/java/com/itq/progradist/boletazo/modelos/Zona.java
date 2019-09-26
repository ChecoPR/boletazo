package com.itq.progradist.boletazo.modelos;

public class Zona {
	
	/**
	 * Propiedades de una zona
	 */
	private int idLugar;
	private int idZona;
	
	public Zona(int idLugar, int idZona) {
		super();
		this.idLugar = idLugar;
		this.idZona = idZona;
	}
	
	public int getIdLugar() {
		return idLugar;
	}
	public void setIdLugar(int idLugar) {
		this.idLugar = idLugar;
	}
	public int getIdZona() {
		return idZona;
	}
	public void setIdZona(int idZona) {
		this.idZona = idZona;
	}
	
	
}
