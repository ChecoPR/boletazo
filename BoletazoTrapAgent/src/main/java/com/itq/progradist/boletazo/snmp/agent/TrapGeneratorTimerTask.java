package com.itq.progradist.boletazo.snmp.agent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

public class TrapGeneratorTimerTask extends TimerTask {
	
	/**
	 * Escribe en el archivo configurado
	 */
	private static final Logger logger = LogManager.getLogger(TrapGeneratorTimerTask.class);
	
	/**
	 * Marca el intervalo de tiempo de la ejecución del proceso de envío de traps
	 */
	private static final int DELAY = 5000;
	
	/**
	 * Entrada de la aplicación. Crea y agenda el proceso de envío de traps. 
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		String log4jConfPath = Config.LOG4J_PROPIERTIES;
		PropertyConfigurator.configure(log4jConfPath);
		TrapGeneratorTimerTask generator = new TrapGeneratorTimerTask();
		generator.schedule();
	}

	/**
	 * Realiza el proceso de envio de traps a la maquina destino
	 */
	@Override
	public void run() {
		logger.info("Enviando PDU con los OIDs a " + Config.LOCAL_ADDRESS + ":" + Config.LOCAL_PORT);
		
		PDU response = TrapGenerator.searchOids(Config.OIDS).getResponse();
		
		logger.info("Se recibió respuesta de " + Config.LOCAL_ADDRESS + ":" + Config.LOCAL_PORT);
		
		if (response == null) {
			logger.error("No se recibió respuesta de " + Config.LOCAL_ADDRESS + ":" + Config.LOCAL_PORT);
			return;
		}
		
		if (response.getErrorStatus() != PDU.noError) {
			logger.error("Error en la respuesta de " + Config.LOCAL_ADDRESS + ":" + Config.LOCAL_PORT + ", Respuesta: " + response.getErrorStatusText() + ", Error: " + response.getErrorStatus());
			return;
		}
		
		Vector<? extends VariableBinding> vbs = response.getVariableBindings();
		
		BoletazoTrap diskTrap = TrapGenerator.createMemoryTrap(vbs);
		BoletazoTrap ramTrap = TrapGenerator.createDiskTrap(vbs);
		
		TrapGenerator.sendTrap(diskTrap);
		TrapGenerator.sendTrap(ramTrap);
	}
	
	/**
	 * Agenda el proceso según el tiempo configurado
	 */
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(this, 0, DELAY);
	}
}