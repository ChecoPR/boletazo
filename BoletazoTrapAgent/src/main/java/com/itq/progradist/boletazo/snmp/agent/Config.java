package com.itq.progradist.boletazo.snmp.agent;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class Config {

	/**
	 * Contraseña de la comunidad SNMP
	 */
	public static final String COMMUNITY = "snmpServer";
	
	/**
	 * Dirección IP de la maquina de destino de los paquetes SNMP
	 */
	public static final String DESTINATION_ADDRESS = "127.0.0.1";
	
	/**
	 * Puerto de destino de los paquetes SNMP
	 */
	public static final int DESTINATION_PORT = 162;
	
	/**
	 * Dirección de la maquina dónde se consultará la MIB
	 */
	public static final String LOCAL_ADDRESS = "localhost";
	
	/**
	 * Puerto de la maquina dónde se consultará la MIB
	 */
	public static final int LOCAL_PORT = 161;
	
	/**
	 * Tiempo de espera de respuesta de la maquina donde se consulta la MIB
	 */
	public static final int TARGET_TIMEOUT = 3000;
	
	/**
	 * Número de reintentos en caso de no haber respuesta por parte 
	 * de la maquina donde se consulta la MIB
	 */
	public static final int TARGET_RETRIES = 1;
	
	/**
	 * Número PDUs máximas por repetición 
	 */
	public static final int PDU_MAX_REPETITIONS = 1;
	public static final int PDU_NON_REPETITIONS = 0;

	
	/**
	 * OID del tamaño de la partición
	 */
	public static final String OID_ID_STORAGE_IN_USE = ".1.3.6.1.2.1.25.2.3.1.6.1";
	
	/**
	 * OID del uso de la partición
	 */
	public static final String OID_ID_STORAGE_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.1";
	
	/**
	 * OID del tamaño de la memoria
	 */
	public static final String OID_ID_RAM_IN_USE = ".1.3.6.1.2.1.25.2.3.1.6.3";
	
	/**
	 * OID del uso de memoria
	 */
	public static final String OID_ID_RAM_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.3";
	
	//Sergio
	
//	public static final String OID_ID_STORAGE_IN_USE = ".1.3.6.1.2.1.25.2.3.1.6.43";
//	public static final String OID_ID_STORAGE_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.43";
//	
//	public static final String OID_ID_RAM_IN_USE = ".1.3.6.1.4.1.2021.4.6.0";
//	public static final String OID_ID_RAM_SIZE = ".1.3.6.1.2.1.25.2.3.1.5.1";
	
	/**
	 * OIDS personalizadas de la maquina de Armando
	 */
	public static final String ARMANDO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.0";
	public static final String ARMANDO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.1";
	
	/**
	 * OIDS personalizadas de la maquina de Mariano
	 */
	public static final String MARIANO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.2";
	public static final String MARIANO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.3";
	public static final String MARIANO_OID_ID_DATABASE_PROCESS = "1.3.6.1.6.3.1.1.4.1.4";
	
	/**
	 * OIDS personalizadas de la maquina de Sergio
	 */
	public static final String SERGIO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.5";
	public static final String SERGIO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.6";
	
	/**
	 * Dirección de el archivo propierties de log4j
	 */
	public static final String LOG4J_PROPIERTIES = "C:\\Users\\arman\\Documents\\uni\\7mo-Semestre\\PROGRAMACION\\boletazo\\BoletazoTrapAgent\\log4j.propierties";
	
	/**
	 * Arreglo de OIDS a consultar
	 */
	public static final VariableBinding[] OIDS = {
			new VariableBinding(new OID(Config.OID_ID_STORAGE_IN_USE)),
			new VariableBinding(new OID(Config.OID_ID_STORAGE_SIZE)),
			new VariableBinding(new OID(Config.OID_ID_RAM_IN_USE)),
			new VariableBinding(new OID(Config.OID_ID_RAM_SIZE))
		};
}
