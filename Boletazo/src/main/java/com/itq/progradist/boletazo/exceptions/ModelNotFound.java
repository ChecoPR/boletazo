package com.itq.progradist.boletazo.exceptions;

/**
 * Se lanza cuando no se encuentra en la base de datos 
 * el apartado solicitado
 * 
 * @author Equipo 5
 *
 */
public class ModelNotFound extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Inicializa la exception con un mensaje de error predeterminado.
	 */
	public ModelNotFound(String name, int id) {
		super("El " + name + " n�mero " + id + " no se encontr� en la base de datos");
	}
}