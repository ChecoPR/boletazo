package com.itq.progradist.boletazo.snmp.agent;

import org.snmp4j.PDU;
import org.snmp4j.smi.Integer32;

public class BoletazoTrap extends PDU {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BoletazoTrap() {
		this.setType(PDU.TRAP);
	    this.setRequestID(new Integer32(123));
	}
}
