package com.itq.progradist.boletazo.ftp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itq.progradist.boletazo.ftp.exceptions.BoletazoFtpClientException;

/**
 * Clase customizada de FTPClient. Contiene la configuración 
 * para conectar con el servidor FTP y algunos métodos útiles
 * 
 * @author Equipo 5
 *
 */
public class BoletazoFtpClient extends FTPClient {
	
	/**
	 * logger del servidor, escribe en ftp-client.log
	 */
	private static final Logger logger = LogManager.getLogger(BoletazoFtpClient.class);
	
	/**
	 * Método para controlar la entrada del usuario
	 */
	private Scanner rd; 
	
	/**
	 * Crea un nuevo cliente FTP para el servidor FTP de boletazo
	 */
	public BoletazoFtpClient() {
		rd = new Scanner(System.in);
	}
	
	/**
	 * Conectar al servidor FTP
	 * 
	 * @throws SocketException
	 * @throws IOException
	 * @throws BoletazoFtpClientException
	 */
	public void connectToBoletazoFtpServer() throws SocketException, IOException, BoletazoFtpClientException {
		this.connect(Config.SERVER, Config.PORT);
		logger.info(this.getReplyString());
		int replyCode = this.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new BoletazoFtpClientException("No se pudo hacer la conexi�n al servidor " + Config.SERVER + ":" + Config.PORT);
        }
	}
	
	/**
	 * Ejecuta el login al servidor FTP
	 * 
	 * @throws IOException
	 * @throws BoletazoFtpClientException
	 */
	public void loginToBoletazoFtpServer() throws IOException, BoletazoFtpClientException {
		boolean login = this.login(Config.USER, Config.PASS);
		logger.info(this.getReplyString());
		if(!login) {
			throw new BoletazoFtpClientException("No se pudo logear al servidor " + Config.SERVER + " con el usuario " + Config.USER);
		}
	}
	
	/**
     * Removes a non-empty directory by delete all its sub files and
     * sub directories recursively. And finally remove the directory.
     */
    public void removeDirectory(String parentDir, String currentDir) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
 
        FTPFile[] subFiles = this.listFiles(dirToList);
 
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }
 
                if (aFile.isDirectory()) {
                    // remove the sub directory
                    this.removeDirectory(dirToList, currentFileName);
                } else {
                    // delete the file
                    boolean deleted = this.deleteFile(filePath);
                    if (deleted) {
                        System.out.println("DELETED the file: " + filePath);
                    } else {
                        System.out.println("CANNOT delete the file: "
                                + filePath);
                    }
                }
            }
 
            // finally, remove the directory itself
            boolean removed = this.removeDirectory(dirToList);
            if (removed) {
                System.out.println("REMOVED the directory: " + dirToList);
            } else {
                System.out.println("CANNOT remove the directory: " + dirToList);
            }
        }
    }

    /**
     * Se muestran los archivos en el directorio actual
     * 
     * @throws IOException
     */
	public void archivos() throws IOException
	{
		String lista [];
		System.out.println("\nArchivos en "+ this.printWorkingDirectory() +":\n");
	    lista = this.listNames();
	    for(int i = 0; i < lista.length;i++)
	    {
	   	 	System.out.println(lista[i]);
	    }
	}

	/**
	 * Mueve un archivo de directorio
	 * 
	 * @param archivo El nombre del archivo que se moverá
	 * @param aux Variable auxiliar
	 * @throws IOException
	 */
	public void menuReubicacion(String archivo, String aux) throws IOException
	{
		int opc = 0;
		boolean termino = false;
		String nueva_ruta = "";
		while(termino == false)
		{
			this.archivos();
			System.out.print("\n1.Abrir directorio\n2.Reubicar aqui\nSeleccion: ");
			opc = FtpClientApp.rd.nextInt();
			switch(opc)
			{
				case 1:
					this.abrirDirectorio();
					break;
				case 2:
					nueva_ruta = this.printWorkingDirectory() + "/" + aux;
					this.rename(archivo,nueva_ruta);
					termino = true;
					logger.info("Archivo " + aux + " reubicado correctamente a " + this.printWorkingDirectory());
					System.out.print("\nArchivo reubicado exitosamente.");
					break;
				default:
					logger.error("Opcion no valida.");
					System.out.print("Opcion no vlaida.");
			}
		}
	}
	
	/**
	 * Cambia el directorio de trabajo al 
	 * directorio espefificado. Solo funciona para ir directorios 
	 * hacia adentro y no hacia afuera
	 * 
	 * @throws IOException
	 */
	public void abrirDirectorio() throws IOException
    {
    	System.out.print("\nAbrir directorio: ");
    	String directorio = rd.next();
		this.changeWorkingDirectory(directorio);
		logger.info("Directorio " + directorio + "abierto, ruta actual: " + this.printWorkingDirectory());
    }
	
	/**
	 * Eliminar un directorio según el nombre especificado
	 * 
	 * @throws IOException
	 */
	public void eliminarDirectorio() throws IOException
    {
    	System.out.print("\nDirectorio a borrar: ");
    	String dir = rd.next();
		this.removeDirectory(this.printWorkingDirectory(), dir);
		logger.info("Directorio " + dir + "eliminado exitosamente.");
    	System.out.print("\nDirectorio eliminado exitosamente.");
    }
	
	/**
	 * Crear un archivo en la raíz
	 * 
	 * @throws IOException
	 */
	public void crearArchivo() throws IOException
    {
    	System.out.print("\nNombre de archivo nuevo: ");
    	String fichero = rd.next();
    	String ruta = Config.BASE_PATH + 
    			"" + fichero + ".txt";
    	File nuevoA = new File(ruta);
    	BufferedWriter bw;
    	if(nuevoA.exists())
    	{
			logger.error("El archivo se encuentra duplicado.");
    		System.out.print("El archivo se encuentra duplicado.");
    	}
    	else
    	{
    		bw = new BufferedWriter(new FileWriter(nuevoA));
			bw.close();
			logger.info("Archivo guardado exitosamente.");
    		System.out.print("\nArchivo guardado exitosamente.");
    	}
    	File file = new File(ruta);
    	FileInputStream input = new FileInputStream(file);
    	this.setFileType(FTP.BINARY_FILE_TYPE);
		this.enterLocalActiveMode();
		logger.info("Archivo creado exitosamente en " + this.printWorkingDirectory());
    	System.out.print("\nArchivo creado exitosamente.");
    	if (!this.storeFile(file.getName(),input))
    	{
			logger.error("Archivo subido exitosamente.");
    		System.out.println("Archivo subido exitosamente.");
    	}
    	nuevoA.delete();	
    }
	
	/**
	 * Crea un directorio en el directorio de trabajo actual
	 * 
	 * @throws IOException
	 */
	public void crearDirectorio() throws IOException
    {
    	System.out.print("\nNombre de directorio nuevo: ");
    	String nom = rd.next();
		this.makeDirectory(nom);
		logger.info("Directorio creado exitosamente en " + this.printWorkingDirectory());
    	System.out.print("\nDirectorio creado exitosamente.");
    }
	
	/**
	 * Cambia el nombre de un directorio
	 * 
	 * @throws IOException
	 */
	public void renombrarDirectorio() throws IOException
    {
    	System.out.print("\nDirectorio a renombrar: ");
    	String viejo = rd.next();
    	System.out.print("Nuevo nombre: ");
    	String nom = rd.next();
		this.rename(viejo, nom);
		logger.info("Directorio renombrado de " + viejo + " a " + nom + ", exitosamente.");
    	System.out.print("\nDirectorio renombrado exitosamente.");
    }
	
	/**
	 * Borra un archivo
	 * 
	 * @throws IOException
	 */
	public void borrarArchivo() throws IOException
    {
    	System.out.print("\nArchivo a eliminar: ");
    	String archivo = rd.next();
		this.deleteFile(archivo);
		logger.info("Archivo " + archivo + " eliminado exitosamente.");
    	System.out.print("\nArchivo eliminado exitosamente.");
    }
	
	/**
	 * Cambia un archivo de directorio
	 * 
	 * @throws IOException
	 */
	public void reubicarArchivo() throws IOException
    {
    	String ruta = this.printWorkingDirectory();
    	System.out.print("\nArchivo a reubicar: ");
    	String arc = rd.next();
    	ruta = ruta + "/" + arc;
    	this.changeWorkingDirectory("/");
    	System.out.print(this.printWorkingDirectory());
    	System.out.println("Seleccione una ruta: ");
    	this.menuReubicacion(ruta,arc);
    }

}
