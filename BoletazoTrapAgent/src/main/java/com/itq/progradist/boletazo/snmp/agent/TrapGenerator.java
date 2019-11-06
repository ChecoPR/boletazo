package com.itq.progradist.boletazo.snmp.agent;

import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class TrapGenerator {
	private static final Logger logger = LogManager.getLogger(TrapGenerator.class);
	
	public static ResponseEvent searchOids(VariableBinding[] oids) {
		ResponseEvent responseEvent = null;
		try {
			Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
			
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString(Config.COMMUNITY));
			target.setVersion(SnmpConstants.version2c);
			target.setAddress(new UdpAddress(Config.LOCAL_ADDRESS + "/" + Config.LOCAL_PORT));
			target.setTimeout(Config.TARGET_TIMEOUT);
			target.setRetries(Config.TARGET_RETRIES);
			
			PDU pdu = new PDU();
			pdu.setType(PDU.GET);
			pdu.setMaxRepetitions(Config.PDU_MAX_REPETITIONS); 
			pdu.setNonRepeaters(Config.PDU_NON_REPETITIONS);
										
			pdu.addAll(oids);
			
			responseEvent = snmp.send(pdu, target);
		} catch (IOException e) {
			logger.error("Error escuchando respuesta de " + Config.LOCAL_ADDRESS + ": " + e.getMessage());
		}
		
		return responseEvent;
	}
	
	public static void sendTrap(BoletazoTrap boletazoTrap) {
		try {
			Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
			
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString(Config.COMMUNITY));
			target.setVersion(SnmpConstants.version2c);
			target.setAddress(new UdpAddress(Config.DESTINATION_ADDRESS + "/" + Config.DESTINATION_PORT));
			target.setTimeout(5000);
			target.setRetries(2);
			
			snmp.send(boletazoTrap, target);
			logger.info("Sent Trap to (IP:Port)=> " + Config.DESTINATION_ADDRESS + ":" + Config.DESTINATION_PORT);
			snmp.close();
		} catch (Exception e) {
			logger.error("Error in Sending Trap to (IP:Port)=> " + Config.DESTINATION_ADDRESS
					+ ":" + Config.DESTINATION_PORT);
			logger.error("Exception Message = " + e.getMessage());
		}
	}
	
	public static BoletazoTrap createDiskTrap(Vector<? extends VariableBinding> vectorVariableBinding) {
		BoletazoTrap boletazoTrap = new BoletazoTrap();
		
		VariableBinding diskUsageBinding = vectorVariableBinding.get(0);
		VariableBinding diskSizeBinding = vectorVariableBinding.get(1);
		
		double diskUsage = Double.parseDouble(diskUsageBinding.getVariable().toString());
		double diskSize = Double.parseDouble(diskSizeBinding.getVariable().toString());
		
		logger.debug("Disco usado: " + diskUsage + ", " + "OID: " + diskUsageBinding.getOid());
		logger.debug("Disco tamanio: " + diskSize + ", " + "OID: " + diskSizeBinding.getOid());
		
		double percentage = (double) (diskUsage / diskSize) * 100.0;
		
		logger.debug("Porcentaje: " + percentage);
		
		boletazoTrap.add(new VariableBinding(SnmpConstants.snmpTrapOID, 
				new OctetString(String.valueOf(percentage)))
			);
		return boletazoTrap;
	}
	
	public static BoletazoTrap createMemoryTrap(Vector<? extends VariableBinding> vectorVariableBinding) {
		BoletazoTrap boletazoTrap = new BoletazoTrap();
		
		VariableBinding ramUsageBinding = vectorVariableBinding.get(2);
		VariableBinding ramSizeBinding = vectorVariableBinding.get(3);
		
		double ramUsage = Double.parseDouble(ramUsageBinding.getVariable().toString());
		double ramSize = Double.parseDouble(ramSizeBinding.getVariable().toString());
		
		logger.debug("RAM usado: " + ramUsage + ", " + "OID: " + ramUsageBinding.getOid());
		logger.debug("RAM tamanio: " + ramSize + ", " + "OID: " + ramSizeBinding.getOid());
		
		double percentage = (double) (ramUsage / ramSize) * 100.0;
		
		logger.debug("Porcentaje: " + percentage);
		
		boletazoTrap.add(new VariableBinding(new OID(Config.ARMANDO_OID_ID_MEMORY_PERCENTAGE), 
				new OctetString(String.valueOf(percentage)))
			);
		
		return boletazoTrap;
	}

}
