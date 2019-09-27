package com.itq.progradist.boletazo.modelos;

public class MetodoPago {
	
	/**
	 * Propiedades de un método de pago
	 */
	private int idMetodoPago;
	private int idUsuario;
	
	public MetodoPago(int idMetodoPago, int idUsuario) {
		super();
		this.idMetodoPago = idMetodoPago;
		this.idUsuario = idUsuario;
	}
	
	public int getIdMetodoPago() {
		return idMetodoPago;
	}
	public void setIdMetodoPago(int idMetodoPago) {
		this.idMetodoPago = idMetodoPago;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	
	
}
