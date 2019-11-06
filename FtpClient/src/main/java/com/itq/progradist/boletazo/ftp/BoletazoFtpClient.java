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
	 * Direcci�n IP del servidor FTP
	 */
	public static final String SERVER = "192.168.1.3";
	
	/**
	 * Puerto del servidor FTP
	 */
	public static final int PORT = 21;
	
	/**
	 * Usuario FTP
	 */
	public static final String USER = "ftpBoletazo";
	
	/**
	 * Contrase�a del usuario FTP
	 */
	public static final String PASS = "test2019";
	
	public static final String BASE_PATH = "C:\\Users\\arman\\Desktop\\";
	
	private Scanner rd; 
	
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
		this.connect(SERVER, PORT);
		logger.info(this.getReplyString());
		int replyCode = this.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new BoletazoFtpClientException("No se pudo hacer la conexi�n al servidor " + SERVER + ":" + PORT);
        }
	}
	
	/**
	 * Ejecuta el login al servidor FTP
	 * 
	 * @throws IOException
	 * @throws BoletazoFtpClientException
	 */
	public void loginToBoletazoFtpServer() throws IOException, BoletazoFtpClientException {
		boolean login = this.login(USER, PASS);
		logger.info(this.getReplyString());
		if(!login) {
			throw new BoletazoFtpClientException("No se pudo logear al servidor " + SERVER + " con el usuario " + USER);
		}
	}
	
	/**
	* Utility to create an arbitrary directory hierarchy on the remote ftp server
	*  
	* @param client
	* @param dirTree  the directory tree only delimited with / chars.  No file name!
	* @throws Exception
	*/
	private void createDirectoryTree(String dirTree ) throws IOException {

	  boolean dirExists = true;

	  //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
	  String[] directories = dirTree.split("/");
	  for (String dir : directories ) {
	    if (!dir.isEmpty() ) {
	      if (dirExists) {
	        dirExists = this.changeWorkingDirectory(dir);
	      }
	      if (!dirExists) {
	        if (!this.makeDirectory(dir)) {
	          throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + this.getReplyString()+"'");
	        }
	        if (!this.changeWorkingDirectory(dir)) {
	          throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + this.getReplyString()+"'");
	        }
	      }
	    }
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

	public void archivos() throws IOException //Se muestran los archivos en el directorio actual
	{
		String lista [];
		System.out.println("\nArchivos en "+ this.printWorkingDirectory() +":\n");
	    lista = this.listNames();
	    for(int i = 0; i < lista.length;i++)
	    {
	   	 	System.out.println(lista[i]);
	    }
	}

	public void menuReubicacion(String archivo,String aux) throws IOException
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
	
	public void abrirDirectorio() throws IOException
    {
    	System.out.print("\nAbrir directorio: ");
    	String directorio = rd.next();
		this.changeWorkingDirectory(directorio);
		logger.info("Directorio " + directorio + "abierto, ruta actual: " + this.printWorkingDirectory());
    }
	
	public void eliminarDirectorio() throws IOException
    {
    	System.out.print("\nDirectorio a borrar: ");
    	String dir = rd.next();
		this.removeDirectory(this.printWorkingDirectory(), dir);
		logger.info("Directorio " + dir + "eliminado exitosamente.");
    	System.out.print("\nDirectorio eliminado exitosamente.");
    }
	
	public void crearArchivo() throws IOException
    {
    	System.out.print("\nNombre de archivo nuevo: ");
    	String fichero = rd.next();
    	String ruta = BoletazoFtpClient.BASE_PATH + 
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
	
	public void crearDirectorio() throws IOException
    {
    	System.out.print("\nNombre de directorio nuevo: ");
    	String nom = rd.next();
		this.makeDirectory(nom);
		logger.info("Directorio creado exitosamente en " + this.printWorkingDirectory());
    	System.out.print("\nDirectorio creado exitosamente.");
    }
	
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
	
	public void borrarArchivo() throws IOException
    {
    	System.out.print("\nArchivo a eliminar: ");
    	String archivo = rd.next();
		this.deleteFile(archivo);
		logger.info("Archivo " + archivo + " eliminado exitosamente.");
    	System.out.print("\nArchivo eliminado exitosamente.");
    }
	
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
