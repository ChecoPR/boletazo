package com.itq.progradist.boletazo.exceptions;

import com.itq.progradist.boletazo.ParamNames;

/**
 * Exception para cuando los datos de la peticion 
 * no contienen el campo "recurso".
 * 
 * @author Equipo 5
 *
 */
public class ParamRecursoNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Inicializa la exception con un mensaje de error predeterminado.
	 */
	public ParamRecursoNotFoundException() {
		super("Parámetro " + ParamNames.Recurso.KEY_NAME + " no encontrado en los datos de la petición");
	}

	/**
	 * Inicializa con un mensaje de error personalizado.
	 * 
	 * @param msg Mensaje de error.
	 */
	public ParamRecursoNotFoundException(String msg) {
		super(msg);
	}
}
