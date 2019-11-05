package com.itq.progradist.boletazo.ftp;

/**
 * Contiene la entrada de la aplicación
 * 
 * @author Equipo 5
 *
 */
public class BoletazoFtpClientMain {
	
	/**
	 * Esta es la entrada de la aplicación. Inicia con la ejecución del 
	 * timer task para generar informes
	 * @param args
	 */
	public static void main(String[] args) {
		InformesTimerTask informesTimerTask = new InformesTimerTask();
		informesTimerTask.schedule();
	}

}
