package com.itq.progradist.boletazo.administrador;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Contiene utilidades para el env�o de correos
 * 
 * @author Equipo 5
 *
 */
public class MailUtils {
	 
	/**
	 * Env�a un correo electr�nico
	 * 
	 * @param host Host desde el que env�a
	 * @param port Puerto desde el que se env�a
	 * @param userName Nombre de usuario
	 * @param password Contrase�a
	 * @param toAddress Direcci�n a la que se env�a
	 * @param subject Asunto del correo
	 * @param message Mensaje del correo
	 * @param multipart 
	 * 
	 * @throws AddressException
	 * @throws MessagingException
	 */
    public void sendEmail(String host, String port,
            final String userName, final String password, String toAddress,
            String subject, String message, Multipart multipart) throws AddressException,
            MessagingException {
 
        // sets SMTP accessMail properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(properties,
           new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(userName, password);
              }
           });
        session.setDebug(true);
 
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        
        msg.setContent(multipart, "text/html; charset=utf-8");
        
        Transport.send(msg);
    }
    
    /**
     * Env�a un correo electr�nico para informar un problema en el sistema
     * 
     * @param descripcion Mensaje del problema
     */
    public void enviarCorreo(String descripcion)
    {

        // outgoing message information
        String subject = "PROBLEMA EN SISTEMA BOLETAZO";
        
        try {
        Multipart multipart = new MimeMultipart();

        MimeBodyPart textPart = new MimeBodyPart();
        String textContent = descripcion;
        textPart.setText(textContent);
        multipart.addBodyPart(textPart);
        
        MailUtils mailer = new MailUtils();
        mailer.sendEmail(MailConfig.HOST, MailConfig.PORT, MailConfig.MAIL_FROM, MailConfig.PASSWORD, MailConfig.MAIL_TO,
                    subject, "PROBLEMA EN SISTEMA", multipart);
        System.out.println("Sent message successfully....");

        } catch (Exception ex) {
            System.out.println("Failed to sent email.");
            ex.printStackTrace();
        }
    }
}