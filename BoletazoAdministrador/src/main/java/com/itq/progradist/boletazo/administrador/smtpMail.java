package com.itq.progradist.boletazo.administrador;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class smtpMail {
	 
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
	 
	        // creates a new session with an authenticator
	        Authenticator auth = new Authenticator() {
	            public PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(userName, password);
	            }
 	        };
	        
	        // Get the Session object.
	        Session session = Session.getInstance(properties,
	           new javax.mail.Authenticator() {
	              protected PasswordAuthentication getPasswordAuthentication() {
	                 return new PasswordAuthentication(userName, password);
	              }
	           });
	        session.setDebug(true);
//	        Session session = Session.getInstance(properties, auth);
	 
	        // creates a new e-mail message
	        Message msg = new MimeMessage(session);
	 
	        msg.setFrom(new InternetAddress(userName));
	        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
	        msg.setRecipients(Message.RecipientType.TO, toAddresses);
	        msg.setSubject(subject);
	        msg.setSentDate(new Date());
	        
	        // set plain text message
	        msg.setContent(multipart, "text/html; charset=utf-8");
	        // sends the e-mail
	        Transport.send(msg);
	    }
	 
	    /**
	     * Test the send html e-mail method
	     *
	     */
	    
	    public void enviarCorreo(String descripcion)
	    {
	    	// SMTP accessMail information
	    	String host = "smtp.gmail.com";
	        String port = "587";
//	        String host = "progradist.local";
//	        String port = "25";
	        String mailFrom = "boletazoprogradist@gmail.com";
	        String password = "test2019";
	 
	        // outgoing message information
	        String mailTo = "marianoesquivel13@gmail.com";
	        String subject = "PROBLEMA EN SISTEMA BOLETAZO";
	        
	        
            // Create the message part
	        
	        try {
            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
	        String textContent = descripcion;
	        textPart.setText(textContent);
	        multipart.addBodyPart(textPart);
            
	        /*MimeBodyPart htmlPart = new MimeBodyPart();
	        
            // Now set the actual message
            // message contains HTML markups
	        String message = "<br><i>HAY UN PROBELEMA CON EL SISTEMA</i><br>";
	        message += "<b>Sr.</b><br>";
	        message += "<font color=red>Hasta ac· es html</font>";
	        htmlPart.setContent(message, "text/html");
	        multipart.addBodyPart(htmlPart);

	        
	        
		     // Se crea el documento
	        Document documento = new Document();

	        // Se crea el OutputStream para el fichero donde queremos dejar el pdf.
	        String dest = "fichero.pdf";
	        FileOutputStream ficheroPdf = new FileOutputStream(dest);

	        // Se asocia el documento al OutputStream y se indica que el espaciado entre
	        // lineas sera de 20. Esta llamada debe hacerse antes de abrir el documento
	        PdfWriter.getInstance(documento,ficheroPdf).setInitialLeading(20);

	        // Se abre el documento.
	        documento.open();

	        documento.add(new Paragraph("Hola crayola",
	        				FontFactory.getFont("arial",   // fuente
	        				22,                            // tama√±o
	        				Font.ITALIC,                   // estilo
	        				BaseColor.GREEN)));             // color
	        documento.add(new Paragraph("Zona baja: 	$100000"));

	        documento.close();
	        
            
            // Part two is attachment
	        MimeBodyPart attachementPart = new MimeBodyPart();
	        attachementPart.attachFile(new File(dest));
	        multipart.addBodyPart(attachementPart);*/

            
            smtpMail mailer = new smtpMail();
	        mailer.sendEmail(host, port, mailFrom, password, mailTo,
	                    subject, "PROBLEMA EN SISTEMA", multipart);
	        System.out.println("Sent message successfully....");

	        } catch (Exception ex) {
	            System.out.println("Failed to sent email.");
	            ex.printStackTrace();
	        }
	    }
	    public static void main(String[] args) {
	    }
	}
