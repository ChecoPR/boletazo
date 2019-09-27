package com.itq.progradist.boletazo.exceptions;

public class MetodoParamNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MetodoParamNotFoundException() {
		super("Parámtero 'metodo' no encontrado en los datos de la petición");
	}

	public MetodoParamNotFoundException(String msg) {
		super(msg);
	}
}
