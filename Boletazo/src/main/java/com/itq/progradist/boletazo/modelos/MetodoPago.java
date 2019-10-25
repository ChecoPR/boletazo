package com.itq.progradist.boletazo.modelos;

public class MetodoPago {
	
	/**
	 * Propiedades de un método de pago
	 */
	private int idMetodoPago;
	private int idUsuario;
	private double saldo;
	
	public MetodoPago(int idMetodoPago, int idUsuario, double saldo) {
		super();
		this.idMetodoPago = idMetodoPago;
		this.idUsuario = idUsuario;
		this.saldo = saldo;
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

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	
	
	
	
}
