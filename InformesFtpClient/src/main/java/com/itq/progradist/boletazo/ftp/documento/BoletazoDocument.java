package com.itq.progradist.boletazo.ftp.documento;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itq.progradist.boletazo.ftp.Config;
import com.itq.progradist.boletazo.ftp.modelos.Evento;

/**
 * Informe de las ventas de los lugares
 * 
 * @author Equipo 5
 *
 */
public class BoletazoDocument extends Document {
	
	/**
	 * logger del servidor, escribe en el archivo configurado
	 */
	private static final Logger logger = LogManager.getLogger(BoletazoDocument.class);
	
	/**
	 * Nombre del directorio del archivo
	 */
	private String dirName;
	
	/**
	 * Nombre del archivo
	 */
	private String pdfName;
	
	/**
	 * Crea un archivo en el directorio y con el nombre especificado
	 * 
	 * @param dirName Directorio del archivo
	 * @param pdfName Nombre del archivo
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public BoletazoDocument(String dirName, String pdfName) throws FileNotFoundException, DocumentException {
		super();
		
		this.dirName = dirName;
		this.pdfName = pdfName; 

        String basePath = Config.BASE_PATH + dirName;
        
        File directory = new File(basePath);
        if(!directory.exists()) {
        	directory.mkdirs();
        }
        
        FileOutputStream ficheroPdf;
        
		ficheroPdf = new FileOutputStream(new File(basePath, pdfName + Config.EXT));
        PdfWriter.getInstance(this,ficheroPdf).setInitialLeading(20);
	}
	
	/**
	 * A�ade el header al archivo
	 * 
	 * @param today Fecha del informe
	 * @param nombre Nombre de la sede
	 * @throws DocumentException
	 */
	public void addBoletazoHeader(String today, String nombre) throws DocumentException {
		this.add(new Paragraph("Informe de ventas del d�a: " + today,
				FontFactory.getFont("arial",   // fuente
				22,                            // tama�o
				Font.ITALIC,                   // estilo
				BaseColor.GREEN)));
		this.add(new Paragraph("Sede: " + nombre,
				FontFactory.getFont("arial",   // fuente
				22,                            // tama�o
				Font.ITALIC,                   // estilo
				BaseColor.GREEN)));
	}
	
	/**
	 * A�ade el pieal informe
	 * 
	 * @param totalVentas Total de las ventas
	 * @throws DocumentException
	 */
	public void addBoletazoFooter(double totalVentas) throws DocumentException {
		this.add(
				new Paragraph(
					String.format("Total: %.2f", totalVentas)
				));		
	}

	/**
	 * A�ade los eventos al informe
	 * 
	 * @param eventos
	 * @throws DocumentException
	 */
	public void addEventos(List<Evento> eventos) throws DocumentException {
		Paragraph paragraph = null;
		for (Evento evento: eventos) {
			paragraph = new Paragraph(
				String.format("Evento id: %d \t Nombre: %s \t Fecha: %s \t Hora: %s \t Total: %.2f", 
					evento.getIdEvento(), 
					evento.getNombre(), 
					evento.getFecha(),
					evento.getHora(),
					evento.getTotal()
				)
			);
			logger.debug(evento.getTotal());
			this.add(paragraph);
		}
		
	}

	/**
	 * Develve el nombre del directorio d�nde se encuentra el 
	 * archivo pero sin la ra�z
	 * @return
	 */
	public String getDirName() {
		return dirName;
	}

	/**
	 * Devuelve el nombre del archivo con extensi�n
	 * 
	 * @return
	 */
	public String getPdfName() {
		return pdfName + Config.EXT;
	}
	
	/**
	 * Devuelve el nombre completo del archivo con la ruta ra�z
	 * 
	 * @return
	 */
	public String getFullName() {
		return Config.BASE_PATH + this.dirName + this.pdfName + Config.EXT; 
	}

}
