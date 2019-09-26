package com.itq.progradist.boletazo.modelos;

public class Evento {
	/**
	 * Propiedades de un evento
	 */
	private int idEvento;
	private int idLugar;
	private String nombre;
	
	public Evento(int idEvento, int idLugar, String nombre) {
		super();
		this.idEvento = idEvento;
		this.idLugar = idLugar;
		this.nombre = nombre;
	}

	public int getIdEvento() {
		return idEvento;
	}
	
	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
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
