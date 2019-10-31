package com.itq.progradist.boletazo.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itq.progradist.boletazo.ftp.documento.BoletazoDocument;
import com.itq.progradist.boletazo.ftp.exceptions.BoletazoFtpClientException;

public class BoletazoFtpClient extends FTPClient {
	
	/**
	 * logger del servidor, escribe en ftp-client.log
	 */
	private static final Logger logger = LogManager.getLogger(BoletazoFtpClient.class);
	
	public static final String SERVER = "192.168.1.2";
	public static final int PORT = 21;
	public static final String USER = "ftpBoletazo";
	public static final String PASS = "test2019";
	
	public void connectToBoletazoFtpServer() throws SocketException, IOException, BoletazoFtpClientException {
		this.connect(SERVER, PORT);
		logger.info(this.getReplyString());
		int replyCode = this.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new BoletazoFtpClientException("No se pudo hacer la conexión al servidor " + SERVER + ":" + PORT);
        }
	}
	
	public void loginToBoletazoFtpServer() throws IOException, BoletazoFtpClientException {
		boolean login = this.login(USER, PASS);
		logger.info(this.getReplyString());
		if(!login) {
			throw new BoletazoFtpClientException("No se pudo logear al servidor " + SERVER + " con el usuario " + USER);
		}
	}
	
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
	* utility to create an arbitrary directory hierarchy on the remote ftp server 
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

}
