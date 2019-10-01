package com.itq.progradist.boletazo.modelos;

public class Apartado {
	private int idApartado;
	private int idUsuario;
	private int idEvento;
	private double pagado;
	private String tiempo;
	
	
	
	public Apartado(int idApartado, int idUsuario, int idEvento, double pagado, String tiempo) {
		super();
		this.idApartado = idApartado;
		this.idUsuario = idUsuario;
		this.idEvento = idEvento;
		this.pagado = pagado;
		this.tiempo = tiempo;
	}
	public int getIdApartado() {
		return idApartado;
	}
	public void setIdApartado(int idApartado) {
		this.idApartado = idApartado;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getIdEvento() {
		return idEvento;
	}
	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}
	public double getPagado() {
		return pagado;
	}
	public void setPagado(double pagado) {
		this.pagado = pagado;
	}
	public String getTiempo() {
		return tiempo;
	}
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}
	
}
