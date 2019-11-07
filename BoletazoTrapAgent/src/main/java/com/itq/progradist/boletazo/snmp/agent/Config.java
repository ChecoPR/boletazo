package com.itq.progradist.boletazo.snmp.agent;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class Config {

	public static final String COMMUNITY = "snmpServer";
	public static final String DESTINATION_ADDRESS = "127.0.0.1";
	public static final int DESTINATION_PORT = 162;
	
	public static final String LOCAL_ADDRESS = "localhost";
	public static final int LOCAL_PORT = 161;
	
	public static final int TARGET_TIMEOUT = 3000;
	public static final int TARGET_RETRIES = 1;
	public static final int PDU_MAX_REPETITIONS = 1;
	public static final int PDU_NON_REPETITIONS = 0;

	//	Armando
//	
	public static final String OID_ID_STORAGE_IN_USE = ".1.3.6.1.2.1.25.2.3.1.6.1";
	public static final String OID_ID_STORAGE_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.1";
	
	public static final String OID_ID_RAM_IN_USE = ".1.3.6.1.2.1.25.2.3.1.6.3";
	public static final String OID_ID_RAM_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.3";
	
	//Sergio
	
//	public static final String OID_ID_STORAGE_IN_USE = ".1.3.6.1.2.1.25.2.3.1.6.43";
//	public static final String OID_ID_STORAGE_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.43";
//	
//	public static final String OID_ID_RAM_IN_USE = ".1.3.6.1.4.1.2021.4.6.0";
//	public static final String OID_ID_RAM_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.1";
	
	public static final String ARMANDO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.0";
	public static final String ARMANDO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.1";
	
	public static final String MARIANO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.2";
	public static final String MARIANO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.3";
	public static final String MARIANO_OID_ID_DATABASE_PROCESS = "1.3.6.1.6.3.1.1.4.1.4";
	
	public static final String SERGIO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.5";
	public static final String SERGIO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.6";
	
	// Aqu� pon la direcci�n al log4j.propierties
	public static final String LOG4J_PROPIERTIES = "C:\\Users\\arman\\Documents\\uni\\7mo-Semestre\\PROGRAMACION\\boletazo\\BoletazoTrapAgent\\log4j.propierties";
	//public static final String LOG4J_PROPIERTIES = "/mnt/E/Documentos/Semestre 7/Programación distribuida/boletazo/BoletazoTrapAgent/log4j.propierties";
	
	public static final VariableBinding[] OIDS = {
			new VariableBinding(new OID(Config.OID_ID_STORAGE_IN_USE)),
			new VariableBinding(new OID(Config.OID_ID_STORAGE_SIZE)),
			new VariableBinding(new OID(Config.OID_ID_RAM_IN_USE)),
			new VariableBinding(new OID(Config.OID_ID_RAM_SIZE))
		};
}
