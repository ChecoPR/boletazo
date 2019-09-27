package com.itq.progradist.boletazo.exceptions;

public class MetodoParamNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MetodoParamNotFoundException() {
		super("Par�mtero 'metodo' no encontrado en los datos de la petici�n");
	}

	public MetodoParamNotFoundException(String msg) {
		super(msg);
	}
}
