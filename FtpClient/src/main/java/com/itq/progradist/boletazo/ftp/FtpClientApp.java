package com.itq.progradist.boletazo.ftp;

import java.util.Scanner;

import org.apache.commons.net.ftp.FTPReply;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FtpClientApp {

	/**
	 * Escribe en el archivo configurado
	 */
	private static final Logger logger = LogManager.getLogger(FtpClientApp.class);
	
	/**
	 * Maneja la entrada del usuario y contraseña 
	 * y la selección del menú
	 */
	static Scanner rd = new Scanner(System.in);
	
	/**
	 * Ejecuta el login del usuario y el despligue del menú
	 */
	public static void execute() {
		System.out.print("Usuario: ");
        final String USUARIO = rd.next(); //Aquíse indica el nombre de usuario dado de alta en el servidor
        System.out.print("Contraseña: ");
        final String PASS = rd.next(); // Su respectiva contraseñaa
        
        BoletazoFtpClient clienteFtp = new BoletazoFtpClient();
         
        try 
        {
        	clienteFtp.connectToBoletazoFtpServer();
            
        	int RESPUESTA = clienteFtp.getReplyCode();
            
        	if (FTPReply.isPositiveCompletion(RESPUESTA)) 
        	{
				logger.info(RESPUESTA);
        		System.out.println("Codigo de retorno: "+RESPUESTA);
        	}
           
        	boolean loginSatisfactorio = clienteFtp.login(USUARIO, PASS);
        	
        	if (loginSatisfactorio) 
        	{
				logger.info("Sesion iniciada exitosamente en servidor FTP" + Config.SERVER + ".");
        		System.out.println("Sesion iniciada con exito.-----------\n");
        		
        		boolean salir = false;
    			while(salir == false)
    			{
    				clienteFtp.archivos();
                	System.out.print("\n1.Crear directorio\n2.Borrar directorio\n3.Abrir Directorio\n4.Renombrar Directorio\n5.Crear archivo\n6.Borrar archivo\n7.Reubicar archivo\n8.Salir\nSeleccion: ");
                	int opc = rd.nextInt();
                	switch(opc)
                	{
                		case 1:
                			clienteFtp.crearDirectorio();
                			break;
                		case 2:
                			clienteFtp.eliminarDirectorio();
                			break;
                		case 3:
                			clienteFtp.abrirDirectorio();
                			break;
                		case 4:
                			clienteFtp.renombrarDirectorio();
                			break;
                		case 5:
                			clienteFtp.crearArchivo();
                			break;
                		case 6:
                			clienteFtp.borrarArchivo();
                			break;
                		case 7:
                			clienteFtp.reubicarArchivo();
                			break;
                		case 8:
                			salir = true;
                			break;
						default:
							logger.error("Opcion ingresada no valida.");
                			System.out.print("Opcion ingresada no valida.");
                	}
                 }
             }
             else
             {
				 logger.error("Sesion no iniciada, verificar que los datos de usuario sean los correctos.");
                 System.out.println("Sesion no iniciada, verificar que los datos de usuario sean los correctos.");
             }                
        } 
        catch (Exception e) 
        {
			logger.error("Error de conexion: " + e.getMessage());
            System.out.println("Error en conexion: "+ e.getMessage());
        }
	}
}
