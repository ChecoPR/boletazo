package com.itq.progradist.boletazo.modelos;

public class Lugar {
	/**
	 * Propiedades de un lugar
	 */
	private int idLugar;
	private String nombre;
	private String estado;
	
	public Lugar(int idLugar, String nombre) {
		super();
		this.idLugar = idLugar;
		this.nombre = nombre;
	}
	
	public Lugar(String nombre, String estado) {
		super();
		this.nombre = nombre;
		this.setEstado(estado);
	}
	
	public Lugar(int idLugar, String nombre, String estado) {
		super();
		this.idLugar = idLugar;
		this.nombre = nombre;
		this.setEstado(estado);
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}
