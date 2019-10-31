package com.itq.progradist.boletazo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import com.itq.progradist.boletazo.database.DatabaseSchema.ApartadoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoAsientoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoTable;
import com.itq.progradist.boletazo.database.DatabaseSchema.EventoZonaTable;
import com.itq.progradist.boletazo.exceptions.ModelNotFound;
import com.itq.progradist.boletazo.modelos.Apartado;
import com.itq.progradist.boletazo.modelos.Lugar;

public class InformesTimerTask extends TimerTask {
	
	/**
	 * logger del servidor, escribe en server.log
	 */
	private static final Logger logger = LogManager.getLogger(ApartadoTimerTask.class);
	
	private static final int DELAY = 10000;
	
	// private static final int DELAY = 86400000; // un día
	
	private Connection connection;
	
	/**
	 * 
	 */
	public InformesTimerTask() {
		this.connection = DatabaseHandler.getConnection();
	}

	@Override
	public void run() {
		String today;
		try {
			today = CommonQueries.getDatabaseCurrentDate(connection);
			
			logger.info("Generando informes de ventas del día " + today);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
			Date date = new Date();
			
			List<Lugar> lugares = CommonQueries.getLugares(connection);
			
			String pdfName;
			Document document;
			for (Lugar lugar : lugares) {
				logger.info("Generando informe de ventas de al sede " + lugar.getIdLugar() + " del día " + today);
				pdfName = dateFormat.format(date) + "-" + lugar.getIdLugar();
				document = this.createPdf(pdfName);
				
				document.open();
				document.add(new Paragraph("Informe de ventas del día: " + today + " Sede: " + lugar.getNombre(),
								FontFactory.getFont("arial",   // fuente
								22,                            // tamaño
								Font.ITALIC,                   // estilo
								BaseColor.GREEN)));
				Paragraph paragraph = null;
				double totalVentas = 0;
				
				List<Apartado> apartados = getTodaysApartadosPagados(lugar);
				for (Apartado apartado : apartados) {
					paragraph = new Paragraph(
						String.format("Venta id: %d \t Evento: %d \t Usuario: %s \t Tiempo: %s \t Pagado: %.2f", 
							apartado.getIdApartado(), 
							apartado.getIdEvento(), 
							apartado.getIdUsuario(),
							apartado.getTiempo(),
							apartado.getPagado()
						)
					);
					document.add(paragraph);
					totalVentas += apartado.getPagado();
				}
				
				document.add(
						new Paragraph(
							String.format("Total: %.2f", totalVentas)
						));
				document.close();
				
				logger.info("Informe de ventas de la sede " + lugar.getIdLugar() + " generado exitosamente, día:" + today);
			}
			
		} catch (SQLException | ModelNotFound e) {
			logger.error("Error al consultar la base de datos: " + e.getMessage());
			logger.catching(e);
		} catch (DocumentException e) {
			logger.error("Error al construir el documento: " + e.getMessage());
			logger.catching(e);
		} catch (FileNotFoundException e) {
			logger.error("Archivo no encontrado: " + e.getMessage());
			logger.catching(e);
		}
	}
	
	public void schedule() {
		Timer timer = new Timer();
		timer.schedule(this, 0, DELAY);
	}
	
	private List<Apartado> getTodaysApartadosPagados(Lugar lugar) throws SQLException, ModelNotFound {
		String sql = getTodaysApartadosPagadosSqlQuery(lugar);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<Apartado> apartados = new ArrayList<>();
		while(rs.next()) {
			apartados.add(new Apartado(
		       		 rs.getInt(ApartadoTable.Cols.ID_APARTADO), 
		       		 rs.getInt(ApartadoTable.Cols.ID_USUARIO),
		       		 rs.getInt(ApartadoTable.Cols.ID_EVENTO),
		       		 rs.getDouble(ApartadoTable.Cols.PAGADO),
		       		 rs.getString(ApartadoTable.Cols.TIEMPO)
		   		 ));
		}
		
		return apartados;
	}
	
	/**
	 * Devuelve la consulta SQL que sirve para obtener un apartado
	 * @param lugar 
	 * 
	 * @param params Parametros de la peticion
	 * 
	 * @return sql Consulta SQL
	 * 
	 */
	private static String getTodaysApartadosPagadosSqlQuery(Lugar lugar) {
		String sql = "SELECT a.*"
				+ " FROM " + ApartadoTable.NAME + " a, " + EventoTable.NAME + " e"
				+ " WHERE DATE(a." + ApartadoTable.Cols.TIEMPO + ") = CURDATE()"
				+ " AND a." + ApartadoTable.Cols.ID_EVENTO + " = e." + EventoTable.Cols.ID_EVENTO
				+ " AND e." + EventoTable.Cols.ID_LUGAR + " = " + lugar.getIdLugar()
				+ " AND a." + ApartadoTable.Cols.PAGADO + " >= ("
					+ "SELECT SUM(ez." + EventoZonaTable.Cols.PRECIO + ")"
					+ " FROM " + EventoAsientoTable.NAME + " ea, " + EventoZonaTable.NAME + " ez"
					+ " WHERE ea." + EventoAsientoTable.Cols.ID_ZONA + " = ez." + EventoZonaTable.Cols.ID_ZONA
					+ " AND ea." + EventoAsientoTable.Cols.ID_EVENTO + " = ez." + EventoZonaTable.Cols.ID_EVENTO
					+ " AND ea." + EventoAsientoTable.Cols.ID_APARTADO + " = a." + ApartadoTable.Cols.ID_APARTADO
					+ " GROUP BY a." + ApartadoTable.Cols.ID_APARTADO 
				+ ")";
		return sql;
	}
	
	private Document createPdf(String nombre) throws FileNotFoundException, DocumentException {
        Document documento = new Document();

        String dest = "C:\\Users\\arman\\Documents\\uni\\7mo-Semestre\\PROGRAMACION\\";
        
        FileOutputStream ficheroPdf;
        
		ficheroPdf = new FileOutputStream(new File(dest, nombre + ".pdf"));
        PdfWriter.getInstance(documento,ficheroPdf).setInitialLeading(20);
        
		return documento;
	}
	
}
