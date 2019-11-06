package com.itq.progradist.boletazo.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itq.progradist.boletazo.ftp.documento.BoletazoDocument;
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
	 * Dirección IP del servidor FTP
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
	 * Contraseña del usuario FTP
	 */
	public static final String PASS = "test2019";
	
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
            throw new BoletazoFtpClientException("No se pudo hacer la conexión al servidor " + SERVER + ":" + PORT);
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
	 * Sube un informe al servidor
	 * 
	 * @param boletazoDocument
	 * @throws IOException
	 * @throws BoletazoFtpClientException
	 */
	public void uploadBoletazoDocument(BoletazoDocument boletazoDocument) throws IOException, BoletazoFtpClientException {
		this.createDirectoryTree(boletazoDocument.getDirName());
		logger.info(this.getReplyString());
		this.changeWorkingDirectory(boletazoDocument.getDirName());
		logger.info(this.getReplyString());
		logger.info("pwd:" + this.printWorkingDirectory());
		logger.info(boletazoDocument.getDirName());
		
		File file = new File(boletazoDocument.getFullName());
		FileInputStream input = new FileInputStream(file);
		this.setFileType(BINARY_FILE_TYPE);
		this.enterLocalActiveMode();
		boolean uploaded = this.storeFile(boletazoDocument.getPdfName(), input);
		if(!uploaded) {
			throw new BoletazoFtpClientException("Falló la subida del archivo con nombre: " + boletazoDocument.getPdfName());
		}
		logger.info("El archivo " + boletazoDocument.getFullName() + " se subió correctamente");
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

}
