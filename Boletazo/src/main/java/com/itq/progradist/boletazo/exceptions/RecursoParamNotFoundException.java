package com.itq.progradist.boletazo.exceptions;

public class RecursoParamNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RecursoParamNotFoundException() {
		super("Parámtero 'metodo' no encontrado en los datos de la petición");
	}

	public RecursoParamNotFoundException(String msg) {
		super(msg);
	}
}
