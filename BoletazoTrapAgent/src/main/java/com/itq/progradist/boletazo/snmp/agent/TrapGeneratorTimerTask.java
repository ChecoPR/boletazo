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
	private static final Logger logger = LogManager.getLogger(TrapGeneratorTimerTask.class);
	
	private static final int DELAY = 10000;

	@Override
	public void run() {
		logger.info("Env�ando PDU con los OIDs a " + Config.DESTINATION_ADDRESS + ":" + Config.DESTINATION_PORT);
		
		PDU response = TrapGenerator.searchOids(Config.OIDS).getResponse();
		
		logger.info("Se recibi� respuesta de " + Config.DESTINATION_ADDRESS + ":" + Config.DESTINATION_PORT);
		
		if (response == null) {
			logger.error("No se recibi� respuesta de " + Config.DESTINATION_ADDRESS + ":" + Config.DESTINATION_PORT);
			return;
		}
		
		if (response.getErrorStatus() != PDU.noError) {
			logger.error("Error en la respuesta de " + Config.DESTINATION_ADDRESS + ":" + Config.DESTINATION_PORT + ", Respuesta: " + response.getErrorStatusText() + ", Error: " + response.getErrorStatus());
			return;
		}
		
		Vector<? extends VariableBinding> vbs = response.getVariableBindings();
		
		BoletazoTrap diskTrap = TrapGenerator.createMemoryTrap(vbs);
		BoletazoTrap ramTrap = TrapGenerator.createDiskTrap(vbs);
		
		TrapGenerator.sendTrap(diskTrap);
		TrapGenerator.sendTrap(ramTrap);
	}
	
	/**
	 * Agenda el proceso seg�n el tiempo configurado
	 */
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(this, 0, DELAY);
	}
	
	public static void main(String args[]) {
		String log4jConfPath = Config.LOG4J_PROPIERTIES;
		PropertyConfigurator.configure(log4jConfPath);
		TrapGeneratorTimerTask generator = new TrapGeneratorTimerTask();
		generator.schedule();
	}
}