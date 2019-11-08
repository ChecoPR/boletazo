package com.itq.progradist.boletazo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itq.progradist.boletazo.database.CommonQueries;
import com.itq.progradist.boletazo.database.DatabaseHandler;
import com.itq.progradist.boletazo.email.SmtpParameters;
import com.itq.progradist.boletazo.exceptions.ModelNotFound;
import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.modelos.Asiento;
import com.itq.progradist.boletazo.modelos.Evento;
import com.itq.progradist.boletazo.modelos.Lugar;
import com.itq.progradist.boletazo.modelos.Usuario;
import com.itq.program.dist.queues.Producer;

public class EmailUtils {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(EmailUtils.class);
	
	public static final String BOLETO_PAGADO_SUBJECT = "Felicidades por tu compra";
	
	public static void sendPaymentEmail( 
            int idUsuario,
            String email,
            int idEvento,
            int idApartado,
            String nombre,
            String subject) throws AddressException, MessagingException {
		
		synchronized (EmailUtils.class) {
			logger.debug("Contruyendo propiedades del correo");
	        // sets SMTP accessMail properties
	        Properties properties = new Properties();
	        properties.put(SmtpParameters.HOST_PROPIERTY_NAME, SmtpParameters.HOST);
	        properties.put(SmtpParameters.PORT_PROPIERTY_NAME, SmtpParameters.PORT);
	        properties.put(SmtpParameters.AUTH_PROPIERTY_NAME, SmtpParameters.AUTH);
	        properties.put(SmtpParameters.TTLS_PROPIERTY_NAME, SmtpParameters.TTLS);
	 
	        logger.debug("Generando autenticación del cliente de correo");
	        // creates a new session with an authenticator
	        Authenticator auth = new Authenticator() {
	            public PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(SmtpParameters.USER, SmtpParameters.PASSWORD);
	            }
	        };
	        
	        logger.debug("Creando sesión");
	        // Get the Session object.
	        Session session = Session.getInstance(properties, auth);
	 
	        logger.debug("Construyendo mensaje");
	        // creates a new e-mail message
	        Message msg = new MimeMessage(session);
	        
	        // constructs from and to address objects
	        InternetAddress fromAddress = new InternetAddress(SmtpParameters.USER); 
	        InternetAddress[] toAddresses = { new InternetAddress(email) };
	 
	        msg.setFrom(fromAddress);
	        msg.setRecipients(Message.RecipientType.TO, toAddresses);
	        msg.setSubject(subject);
	        msg.setSentDate(new Date());
	        
	        logger.debug("Construyendo PDF");
	        MimeBodyPart attachementPart = new MimeBodyPart();
	        try {
				attachementPart.attachFile(buildPdf(idUsuario, nombre, idApartado,idEvento));
			} catch (IOException e) {
				logger.error("Error en el PDF: " + e.getMessage());
				logger.catching(e);
			}
	        Multipart multipart = new MimeMultipart();
	        
	        multipart.addBodyPart(attachementPart);
	        
	        msg.setContent(multipart, SmtpParameters.MYME_TYPE);
	        
	        logger.debug("Enviando mensaje");
	        // sends the e-mail
	        Transport.send(msg);
	        logger.debug("Mensaje enviado");
		}
    }
	
	private static File buildPdf(int idUsuario, String nombre, int idApartado, int idEvento) {
		 // Se crea el documento
        Document documento = new Document();

        // Se crea el OutputStream para el fichero donde queremos dejar el pdf.
        String dest = "C:/Users/ropea/Documents/fichero.pdf";
        
        FileOutputStream ficheroPdf;
        
		try {
			ficheroPdf = new FileOutputStream(dest);
			
	        // Se asocia el documento al OutputStream y se indica que el espaciado entre
	        // lineas sera de 20. Esta llamada debe hacerse antes de abrir el documento
	        PdfWriter.getInstance(documento,ficheroPdf).setInitialLeading(20);
	
	        // Se abre el documento.
	        documento.open();
			documento.add(new Paragraph("Recibo de su compra",
							FontFactory.getFont("arial",   // fuente
							22,                            // tamaÃ±o
							Font.ITALIC,                   // estilo
							BaseColor.GREEN)));
			
			Paragraph idParagraph = new Paragraph(
					String.format("ID Usuario: %d", idUsuario)
				);
			documento.add(idParagraph);
			
			Connection connection = DatabaseHandler.getConnection();
			
			Evento evento = CommonQueries.getEventoById(connection, idEvento);
			Paragraph eventoParagraph = new Paragraph(
					String.format("Evento: %s , Fecha: %s, Hora: %s", 
							evento.getNombre(), 
							evento.getFecha(), 
							evento.getHora())
				);
			documento.add(eventoParagraph);
			
			Lugar lugar = CommonQueries.getLugarById(DatabaseHandler.getConnection(), evento.getIdLugar());
			Paragraph lugarParagraph = new Paragraph(
					String.format("Lugar: %s, Estado: %s", lugar.getNombre(), lugar.getEstado())
				);
			documento.add(lugarParagraph);
			
			List<Asiento> asientos = CommonQueries.getAsientosOfApartado(connection, idApartado);
			
			String asientosString = "";
			
			for (Asiento asiento : asientos) {
				asientosString += "Asiento: " + asiento.getIdAsiento() + " - Zona: " + asiento.getIdZona() + ", ";
			}
			
			Paragraph asientosParagraph = new Paragraph(
					String.format("Asientos: %s", asientosString)
				);
			documento.add(asientosParagraph);
			
			double importe = CommonQueries.calculateImporteOf(connection, idApartado, idEvento); 
			documento.add(new Paragraph("Importe: $" + importe));
			
			Paragraph greetingsParagraph = new Paragraph(
					String.format("Gracias por tu compra, %s", nombre)
				);
			documento.add(greetingsParagraph);
			
		} catch (DocumentException e) {
			logger.error("Error en el PDF: " + e.getMessage());
			logger.catching(e);
		} catch (FileNotFoundException e) {
			logger.error("Error en el PDF: " + e.getMessage());
			logger.catching(e);
		} catch (SQLException e) {
			logger.error("Error en el PDF: " + e.getMessage());
			logger.catching(e);
		} catch (ModelNotFound e) {
			logger.error("Error en el PDF: " + e.getMessage());
			logger.catching(e);
		}

        documento.close();
        
        return new File(dest);
	}
}
