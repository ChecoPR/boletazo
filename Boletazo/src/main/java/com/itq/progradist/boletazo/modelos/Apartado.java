package com.itq.progradist.boletazo.modelos;

public class Apartado {
	private int idApartado;
	private int idUsuario;
	
	
	
	public Apartado(int idApartado, int idUsuario) {
		super();
		this.idApartado = idApartado;
		this.idUsuario = idUsuario;
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
	
}
