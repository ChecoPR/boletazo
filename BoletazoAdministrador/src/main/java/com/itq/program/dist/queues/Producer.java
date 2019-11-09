/**
 * 
 */
package com.itq.program.dist.queues;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Emisor de mensajes jms.
 */
public class Producer {

    /** Contexto de comunicaci�n JNDI. */
    private Context ctx = null;

    /** Connection al �rbol JNDI. */
    private Connection conn = null;

    /**
     * @param args
     */
    public static void main(String[] args) {
//        Producer producer = new Producer();
//        producer.send("localhost", "1099", "B", );
//        System.out.println("Mensaje enviado");
    }

    /**
     * Crea un contexto inicial de comunicaci�n JMS.
     * 
     * @param servidor
     *            Servidor JNDI.
     * @param puerto
     *            Puerto JNDI.
     * @return
     * @throws NamingException Potencial excepci�n.
     */
    private Context createInitialContext(
            final String servidor, final String puerto) 
            throws NamingException  {
        InitialContext initCtx = null;
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, 
                "org.jnp.interfaces.NamingContextFactory");
        props.put(Context.URL_PKG_PREFIXES, 
                "jboss.naming:org.jnp.interfaces");
        props.put(Context.PROVIDER_URL, 
                String.format("jnp://%s:%s", servidor, puerto));
        initCtx = new InitialContext(props);
        return initCtx;
    }

    /**
     * Envia un mensaje a una cola jms.
     * 
     * @param servidor
     *            Servidor JNDI.
     * @param puerto
     *            Puerto JNDI.
     * @param queueName
     *            Nombre de la cola.
     * @param mensaje
     *            Mensaje a enviar.
     * @throws NamingException Excepci�n potencial.
     * @throws JMSException Excepci�n potencial.
     */
    public void send(final String servidor, final String puerto, 
            final String queueName, final String mensaje) {
        // prepara el nombre del recurso JNDI.
        final StringBuffer destino = new StringBuffer("queue/");
        destino.append(queueName);
        ConnectionFactory cf = null;
        try {
            this.ctx = createInitialContext(servidor, puerto);

            // Se obtienen los recursos del �rbol JNDI.
            cf = (ConnectionFactory) this.ctx.lookup("/ConnectionFactory");
            final Queue queue = (Queue) this.ctx.lookup(destino.toString());
    
            // A partir de la f�brica se establecen los objetos de comunicaci�n.
            this.conn = cf.createConnection();
            final Session session = 
                    this.conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(queue);
    
            // Se prepara el mensaje y se env�a.
            TextMessage textMessage = session.createTextMessage(mensaje);
            messageProducer.send(textMessage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.ctx != null) {
                try {
                    this.ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}