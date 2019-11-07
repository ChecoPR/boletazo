package com.itq.program.dist.queues;


import java.util.Properties;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.smi.Variable;

import com.itq.progradist.boletazo.administrador.MailUtils;
import com.itq.progradist.boletazo.administrador.Receiver;


public class Consumer implements MessageListener {

	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(Receiver.class);
	
    /**
     * Contexto de comunicación JMS.
     */
    private Context ctx = null;

    /**
     * Conexión a la queue.
     */
    private Connection connection = null;

    /**
     * Programa principal para leer mensajes de queues.
     * 
     * @param args Argumentos para la lectura en formato: <servidor> <puerto>
     *            <nombreQueue>
     */
    
    public static void main(String args[]) {
    	    Consumer consumer = new Consumer();
            consumer.consume("localhost", "1099", "A");
    }

    /**
     * Obtiene el contexto inicial JNDI.
     * @param servidor Servidor JNDI.
     * @param puerto Puerto del servicio JNDI.
     * @return Contexto inicial.
     * @throws NamingException
     */
    public Context getInitialContext(final String servidor, final String puerto)
            throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.jnp.interfaces.NamingContextFactory");
        props.put(Context.URL_PKG_PREFIXES,
                "org.jboss.naming:org.jnp.interfaces");
        props.put(Context.PROVIDER_URL, "jnp://" + servidor + ":" + puerto);
        return new InitialContext(props);
    }

    /**
     * Prepara el consumo de mensajes en una queue.
     * 
     * @param servidor Servidor del queue manager.
     * @param puerto Puerto jndi.
     * @param nombreQueue Nombre de la queue.
     */
    public void consume(final String servidor, final String puerto,
            final String nombreQueue) {
        String destinationName = "queue/" + nombreQueue;
        ConnectionFactory cf = null;
        try {
            this.ctx = getInitialContext(servidor, puerto);
            cf = (ConnectionFactory) ctx.lookup("/ConnectionFactory");
            Queue queue = (Queue) ctx.lookup(destinationName);
            this.connection = cf.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(queue);
            messageConsumer.setMessageListener(this);
            connection.start();
            Scanner keyIn = new Scanner(System.in);
            System.out.println("Servidor [" + servidor + ":" + puerto + ":" + nombreQueue + 
                    "] escuchando. Escribe   una tecla para salir \n");
            keyIn.next();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            freeResources();
        }
    }

    /**
     * Libera recursos utilizados.
     */
    public static void mail(String descripcion){
		MailUtils.enviarCorreo(descripcion);
    }
    
    private void freeResources() {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            text = text.trim();
            String parts[] = text.split(",");
            System.out.println("Mensaje recibido: [" + text + "]");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
















