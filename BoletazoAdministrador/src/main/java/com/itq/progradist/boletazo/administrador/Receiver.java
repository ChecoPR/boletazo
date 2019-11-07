package com.itq.progradist.boletazo.administrador;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

/**
 * @author 
 * 
 */
public class Receiver implements CommandResponder {
	private static final Logger logger = LogManager.getLogger(Receiver.class);
	
	private MultiThreadedMessageDispatcher dispatcher;
	private Snmp snmp = null;
	private Address listenAddress;
	private ThreadPool threadPool;

	MailUtils correo = new MailUtils();
	
	public static void main(String[] args) {
		
		String log4jConfPath = Config.LOG4J_PROPIERTIES;
		PropertyConfigurator.configure(log4jConfPath);
		new Receiver().run();
	}

	private void run() {
		try {
			init();
			snmp.addCommandResponder(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void init() throws UnknownHostException, IOException {
		threadPool = ThreadPool.create("Trap", 10);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool,
				new MessageDispatcherImpl());
		
		listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:" + Config.ADDRESS + "/" + Config.PORT));
		TransportMapping<?> transport;
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping(
					(UdpAddress) listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping(
					(TcpAddress) listenAddress);
		}
		
		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.listen();
	}

	public void processPdu(CommandResponderEvent crEvent) {
		PDU pdu = crEvent.getPDU();
		logger.info("===== NEW SNMP 2/3 TRAP RECEIVED FROM " + crEvent.getPeerAddress() + " ====");

		logger.info("errorStatus " + String.valueOf(pdu.getErrorStatus()));
		logger.info("errorIndex "+ String.valueOf(pdu.getErrorIndex()));
		logger.info("requestID " +String.valueOf(pdu.getRequestID()));
		logger.info("pduType " + String.valueOf(PDU.TRAP));
		logger.info("communityString " + new String(crEvent.getSecurityName()));

	    Vector<? extends VariableBinding> varBinds = pdu.getVariableBindings();
	    if (varBinds != null && !varBinds.isEmpty()) {
	    	Iterator<? extends VariableBinding> varIter = varBinds.iterator();
	    	
	    	while (varIter.hasNext()) 
	    	{
	    		logger.info("-----------");
		        VariableBinding vb = varIter.next();
	
		        String syntaxstr = vb.getVariable().getSyntaxString();
		        int syntax = vb.getVariable().getSyntax();
		        logger.info( "OID: " + vb.getOid());
		        
		        // Checa si el trap de la memoria o del disco
		        if (vb.getOid().toString().equals(Config.ARMANDO_OID_ID_MEMORY_PERCENTAGE)) {
			        logger.info("Porcentaje de Memoria de Armando: " + vb.getVariable());
			        if(Double.parseDouble(vb.getVariable().toString()) > Config.ARMANDO_MEMORY_LIMIT)
			        {
			        	correo.enviarCorreo("Limite de disco usado excedido en equipo de Armando\n Usado: " + vb.getVariable());
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		        } else if (vb.getOid().toString().equals(Config.ARMANDO_OID_ID_DISK_PERCENTAGE)) {
			        logger.info("Porcentaje de Disco de Armando: " + vb.getVariable());
			        if(Double.parseDouble(vb.getVariable().toString()) > Config.ARMANDO_DISK_LIMIT)
			        {
			        	correo.enviarCorreo("Limite de disco usado excedido en equipo de Armando\n Usado: " + vb.getVariable());
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		    	}else if (vb.getOid().toString().equals(Config.MARIANO_OID_ID_MEMORY_PERCENTAGE)) {
			        logger.info("Porcentaje de Memoria de Mariano: " + vb.getVariable());
			        if(Double.parseDouble(vb.getVariable().toString()) > Config.MARIANO_MEMORY_LIMIT)
			        {
			        	correo.enviarCorreo("Limite de memoria usada excedido en equipo de Mariano\n Usado: " + vb.getVariable());
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		        } else if (vb.getOid().toString().equals(Config.MARIANO_OID_ID_DISK_PERCENTAGE)) {
			        logger.info("Porcentaje de Disco de Mariano: " + vb.getVariable());
			        if(Double.parseDouble(vb.getVariable().toString()) > Config.MARIANO_DISK_LIMIT)
			        {
			        	correo.enviarCorreo("Limite de disco usado excedido en equipo de Mariano\n Usado: " + vb.getVariable());
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		        } else if (vb.getOid().toString().equals(Config.MARIANO_OID_ID_DATABASE_PROCESS)) {
			        logger.info("Estado de la base de datos: " + vb.getVariable());
			        if(vb.getVariable().toString() == "Inactivo")
			        {
			        	correo.enviarCorreo("Base de datos inactiva");
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		        }else if (vb.getOid().toString().equals(Config.SERGIO_OID_ID_MEMORY_PERCENTAGE)) {
			        logger.info("Porcentaje de Memoria de Sergio: " + vb.getVariable());
			        if(Double.parseDouble(vb.getVariable().toString()) > Config.SERGIO_MEMORY_LIMIT)
			        {
			        	correo.enviarCorreo("Limite de memoria usada excedido en equipo de Sergio\n Usado: " + vb.getVariable());
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		        }else if (vb.getOid().toString().equals(Config.SERGIO_OID_ID_DISK_PERCENTAGE)) {
			        logger.info("Porcentaje de Disco de Sergio: " + vb.getVariable());
			        if(Double.parseDouble(vb.getVariable().toString()) > Config.SERGIO_DISK_LIMIT)
			        {
			        	correo.enviarCorreo("Limite de disco usado excedido en equipo de Sergio\n Usado: " + vb.getVariable());
			        }
			        logger.info("syntaxstring: " + syntaxstr);
			        logger.info("syntax: " + syntax);
		        }
		        logger.info("-----------");
	    	}

	    }
	    System.out.println("==== TRAP END ===");
	}
}