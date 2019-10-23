package com.itq.progradist.boletazo.modelos;

public class Asiento {
	
	/**
	 * Propiedades de un asiento
	 */
	private boolean estado;
	private int idAsiento;
	private int idZona;
	private int idEvento;
	
	public Asiento(boolean estado, int idAsiento, int idZona, int idEvento) {
		super();
		this.estado = estado;
		this.idAsiento = idAsiento;
		this.idZona = idZona;
		this.setIdEvento(idEvento);
	}
	
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public int getIdAsiento() {
		return idAsiento;
	}
	public void setIdAsiento(int idAsiento) {
		this.idAsiento = idAsiento;
	}
	public int getIdZona() {
		return idZona;
	}
	public void setIdZona(int idZona) {
		this.idZona = idZona;
	}

	public int getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}
	
	
	
	
}
