package com.itq.progradist.boletazo.administrador;

public class Config {

	/**
	 * OIDS personalizadas de la maquina de Armando
	 */
	public static final String ARMANDO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.0";
	public static final String ARMANDO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.1";
	
	/**
	 * Límite superior del porcentaje de almacenamiento en disco para la maquina de Armando
	 */
	public static final double ARMANDO_DISK_LIMIT = 50.00;
	
	/**
	 * Límite superior del porcentaje de uso de memoria para la maquina de Armando
	 */
	public static final double ARMANDO_MEMORY_LIMIT = 50.00;
	
	/**
	 * OIDS personalizadas de la maquina de Mariano
	 */
	public static final String MARIANO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.2";
	public static final String MARIANO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.3";
	public static final String MARIANO_OID_ID_DATABASE_PROCESS = "1.3.6.1.6.3.1.1.4.1.4";
	
	/**
	 * Límite superior del porcentaje de uso de memoria para la maquina de Mariano
	 */
	public static final double MARIANO_DISK_LIMIT = 50.00;
	public static final double MARIANO_MEMORY_LIMIT = 30.00;
	
	/**
	 * OIDS personalizadas de la maquina de Sergio
	 */
	public static final String SERGIO_OID_ID_DISK_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.5";
	public static final String SERGIO_OID_ID_MEMORY_PERCENTAGE = "1.3.6.1.6.3.1.1.4.1.6";
	
	/**
	 * Límite superior del porcentaje de uso de memoria para la maquina de Sergio
	 */
	public static final double SERGIO_DISK_LIMIT = 50.00;
	public static final double SERGIO_MEMORY_LIMIT = 50.00;
	
	/**
	 * Dirección IP de la máquina que escucha la llegada de traps
	 */
	public static final String ADDRESS = "127.0.0.1";
	
	/**
	 * Puerto de la máquina que escucha la llegada de traps
	 */
	public static final String PORT = "162";
	
	/**
	 * Ubicación del archivo de configuración de log4j
	 */
	public static final String LOG4J_PROPIERTIES = "C:\\Users\\arman\\Documents\\uni\\7mo-Semestre\\PROGRAMACION\\boletazo\\BoletazoAdministrador\\log4j.propierties";
	
	
	
}
