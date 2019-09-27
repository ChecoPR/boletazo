package com.itq.progradist.boletazo.exceptions;

public class RecursoParamNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RecursoParamNotFoundException() {
		super("Par�mtero 'metodo' no encontrado en los datos de la petici�n");
	}

	public RecursoParamNotFoundException(String msg) {
		super(msg);
	}
}
