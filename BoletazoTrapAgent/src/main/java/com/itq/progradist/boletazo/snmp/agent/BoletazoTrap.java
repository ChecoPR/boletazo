package com.itq.progradist.boletazo.snmp.agent;

import org.snmp4j.PDU;
import org.snmp4j.smi.Integer32;

/**
 * Clase customizada de PDU para la creación de traps 
 * para monitorear el sistema de boletazo
 * 
 * @author Equipo 5
 *
 */
public class BoletazoTrap extends PDU {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Crea un nuevo trap para monitorear el sistema de boletazo
	 */
	public BoletazoTrap() {
		this.setType(PDU.TRAP);
	    this.setRequestID(new Integer32(123));
	}
}
