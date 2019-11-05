package com.itq.progradist.snmp.agent;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class TrapGenerator {
	private static final Logger logger = LogManager.getLogger(TrapGenerator.class);
	
	public static ResponseEvent sendPdu() {
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
			pdu.setType(PDU.GETNEXT);
			pdu.setMaxRepetitions(Config.PDU_MAX_REPETITIONS); 
			pdu.setNonRepeaters(Config.PDU_NON_REPETITIONS);
										
			VariableBinding[] array = {new VariableBinding(new OID(Config.OID_STORAGE))};
			pdu.addAll(array);
		
			responseEvent = snmp.send(pdu, target);
		} catch (IOException e) {
			logger.error("Error escuchando respuesta de " + Config.LOCAL_ADDRESS + ": " + e.getMessage());
		}
		
		return responseEvent;
	}
	
	public static void sendTrap(VariableBinding vb) {
		try {
			Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
			
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString(Config.COMMUNITY));
			target.setVersion(SnmpConstants.version2c);
			target.setAddress(new UdpAddress(Config.DESTINATION_ADDRESS + "/" + Config.DESTINATION_PORT));
			target.setTimeout(5000);
			target.setRetries(2);
			
			
			PDU snmpPDU = createPduTrap(vb);
			
			snmp.send(snmpPDU, target);
			logger.info("Sent Trap to (IP:Port)=> " + Config.DESTINATION_ADDRESS + ":" + Config.DESTINATION_PORT);
			snmp.close();
		} catch (Exception e) {
			logger.error("Error in Sending Trap to (IP:Port)=> " + Config.DESTINATION_ADDRESS
					+ ":" + Config.DESTINATION_PORT);
			logger.error("Exception Message = " + e.getMessage());
		}
	}
	
	private static PDU createPduTrap(VariableBinding vb) {
	    PDU pdu = new PDU();
	    pdu.setType(PDU.TRAP);
	    pdu.setRequestID(new Integer32(123));
	    pdu.add(vb);
	    return pdu;
	}

}
