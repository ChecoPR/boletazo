package com.itq.progradist.boletazo.ftp;

/**
 * Contiene la entrada de la aplicaci�n
 * 
 * @author Equipo 5
 *
 */
public class BoletazoFtpClientMain {
	
	/**
	 * Esta es la entrada de la aplicaci�n. Inicia con la ejecuci�n del 
	 * timer task para generar informes
	 * @param args
	 */
	public static void main(String[] args) {
		InformesTimerTask informesTimerTask = new InformesTimerTask();
		informesTimerTask.schedule();
	}

}
