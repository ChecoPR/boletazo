package com.itq.progradist.boletazo.modelos;

public class Evento {
	/**
	 * Propiedades de un evento
	 */
	private int idEvento;
	private int idLugar;
	private String nombre;
	private String fecha;
	private String hora;
	
	public Evento(int idEvento, int idLugar, String nombre, String fecha, String hora) {
		super();
		this.idEvento = idEvento;
		this.idLugar = idLugar;
		this.nombre = nombre;
		this.fecha = fecha;
		this.hora = hora;
	}
	
	public Evento(int idLugar, String nombre, String fecha, String hora) {
		super();
		this.idLugar = idLugar;
		this.nombre = nombre;
		this.fecha = fecha;
		this.hora = hora;
	}
	
	public Evento(String nombre, String fecha, String hora) {
		super();
		this.nombre = nombre;
		this.fecha = fecha;
		this.hora = hora;
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

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}
	
}
