package com.itq.progradist.boletazo.exceptions;

/**
 * Se lanza cuando no se encuentra en la base de datos 
 * el apartado solicitado
 * 
 * @author Equipo 5
 *
 */
public class UsuarioNotFound extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Inicializa la exception con un mensaje de error predeterminado.
	 */
	public UsuarioNotFound(int idUsuario) {
		super("El usuario número " + idUsuario + " no se encontró en la base de datos");
	}
}