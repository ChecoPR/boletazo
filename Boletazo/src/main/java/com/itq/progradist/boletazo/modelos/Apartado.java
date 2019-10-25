package com.itq.progradist.boletazo.modelos;

/**
 * Clase que modela un registro de la tabla apartado.
 * 
 * @author Equipo 5
 *
 */
public class Apartado {
	
	/**
	 * ID del apartado
	 */
	private int idApartado;
	
	/**
	 * ID del usuario que hizo este apartado
	 */
	private int idUsuario;
	
	/**
	 * ID del evento del que se estan apartando los boletos
	 */
	private int idEvento;
	
	/**
	 * Cantidad pagada del apartado
	 */
	private double pagado;
	
	/**
	 * Tiempo en que se registró el apartado
	 */
	private String tiempo;
	
	/**
	 * Importe del apartado
	 */
	private double importe;
	
	/**
	 * Inicializa un Apartado con todos sus campos.
	 * 
	 * @param idApartado
	 * @param idUsuario
	 * @param idEvento
	 * @param pagado
	 * @param tiempo
	 */
	public Apartado(int idApartado, int idUsuario, int idEvento, double pagado, String tiempo) {
		super();
		this.idApartado = idApartado;
		this.idUsuario = idUsuario;
		this.idEvento = idEvento;
		this.pagado = pagado;
		this.tiempo = tiempo;
		this.setImporte(0);
	}
	
	/**
	 * Retorna el ID del apartado
	 * 
	 * @return idApartado
	 */
	public int getIdApartado() {
		return idApartado;
	}
	
	/**
	 * Asigna el ID del apartado
	 * 
	 * @param idApartado ID de apartado que se quiere asignar
	 */
	public void setIdApartado(int idApartado) {
		this.idApartado = idApartado;
	}
	
	/**
	 * Retorna el ID del usuario que hizo este apartado
	 * 
	 * @return idUsuario
	 */
	public int getIdUsuario() {
		return idUsuario;
	}
	
	/**
	 * 
	 * @param idUsuario
	 */
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getIdEvento() {
		return idEvento;
	}
	
	/**
	 * 
	 * @param idEvento
	 */
	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getPagado() {
		return pagado;
	}
	
	/**
	 * 
	 * @param pagado
	 */
	public void setPagado(double pagado) {
		this.pagado = pagado;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTiempo() {
		return tiempo;
	}
	
	/**
	 * 
	 * @param tiempo
	 */
	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getImporte() {
		return importe;
	}

	/**
	 * 
	 * @param importe
	 */
	public void setImporte(double importe) {
		this.importe = importe;
	}
}
