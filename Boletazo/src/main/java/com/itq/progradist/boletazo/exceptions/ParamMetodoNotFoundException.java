package com.itq.progradist.boletazo.exceptions;

import com.itq.progradist.boletazo.ParamNames;

/**
 * Exception para cuando los datos de la peticion 
 * no contienen el campo "metodo".
 * 
 * @author Equipo 5
 *
 */
public class ParamMetodoNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Inicializa la exception con un mensaje de error predeterminado.
	 */
	public ParamMetodoNotFoundException() {
		super("Parámtero " + ParamNames.Metodo.KEY_NAME + " no encontrado en los datos de la petición");
	}

	/**
	 * Inicializa con un mensaje de error personalizado.
	 * 
	 * @param msg Mensaje de error.
	 */
	public ParamMetodoNotFoundException(String msg) {
		super(msg);
	}
}
