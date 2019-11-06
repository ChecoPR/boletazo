package com.itq.progradist.boletazo.ftp.exceptions;

/**
 * Error en la comunicación con el servidor FTP
 * 
 * @author Equipo 5
 *
 */
public class BoletazoFtpClientException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Inicializa el error con un mensaje personalizado
	 * 
	 * @param msg
	 */
	public BoletazoFtpClientException(String msg) {
		super(msg);
	}

}
