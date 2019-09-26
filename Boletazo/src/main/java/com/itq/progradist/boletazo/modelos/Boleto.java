package com.itq.progradist.boletazo.modelos;

public class Boleto {
	
	/**
	 * Propiedades de un boleto
	 */
	private int idApartado;
	private int idAsiento;
	private int idBoleto;
	
	public Boleto(int idApartado, int idAsiento, int idBoleto) {
		super();
		this.idApartado = idApartado;
		this.idAsiento = idAsiento;
		this.idBoleto = idBoleto;
	}
	
	public int getIdApartado() {
		return idApartado;
	}
	public void setIdApartado(int idApartado) {
		this.idApartado = idApartado;
	}
	public int getIdAsiento() {
		return idAsiento;
	}
	public void setIdAsiento(int idAsiento) {
		this.idAsiento = idAsiento;
	}
	public int getIdBoleto() {
		return idBoleto;
	}
	public void setIdBoleto(int idBoleto) {
		this.idBoleto = idBoleto;
	}
	
	

}
