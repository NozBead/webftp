package pain.flopbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.net.ftp.FTPFile;

import pain.flopbox.ftp.Server;

public class ServerMock extends Server {
	
	public ServerMock() {
		super("", "localhost");
	}

	String recievedPath;
	String recievedUsername;
	String recievedPassword;
	String pathMethod;
	String renameFrom;
	String renameTo;
	byte[] recieved;
	byte[] sent = new byte[1024];
	
	@Override
	public void connect() throws IOException {}
	@Override
	public void disconnect() throws IOException {}
	@Override
	public boolean setup() throws IOException {return true;}
	
	@Override
	public FTPFile[] listFiles(String path) throws IOException {
		if ("notfound".equals(path)) {
			return new FTPFile[] {};
		}
		FTPFile f1 = new FTPFile();
		FTPFile f2 = new FTPFile();
		f1.setName("fileone");
		f1.setType(1);
		f2.setName("filetwo");
		f1.setType(0);
		return new FTPFile[] {f1,f2};
	}
	
	@Override
	public boolean delDir(String path) throws IOException {
		if ("forbidden".equals(path)) {
			return false;
		}
		recievedPath = path;
		pathMethod = "delete";
		return true;
	}
	
	@Override
	public boolean delFile(String path) throws IOException {
		if ("forbidden".equals(path)) {
			return false;
		}
		recievedPath = path;
		pathMethod = "delete";
		return true;
	}
	
	@Override
	public boolean login(String username, String password) throws IOException {
		if ("badlog".equals(username)) {
			return false;
		}
		recievedPassword = password;
		recievedUsername = username;
		return true;
	}
	
	@Override
	public boolean makeDirectory(String path) throws IOException {
		if ("forbidden".equals(path)) {
			return false;
		}
		recievedPath = path;
		pathMethod = "mkdir";
		return true;
	}

	@Override
	public boolean renameFile(String path, String newName) throws IOException {
		if ("forbidden".equals(path)) {
			System.out.println(path + " " + newName);
			return false;
		}
		renameFrom = path;
		renameTo = newName;
		return true;
	}
	
	@Override
	public InputStream retrFile(String path) throws IOException {
		if ("forbidden".equals(path)) {
			return null;
		}
		recievedPath = path;
		Random rng = new Random();
		rng.nextBytes(sent);
		return new ByteArrayInputStream(sent);
	}
	
	@Override
	public boolean storFile(String path, InputStream in) throws IOException {
		recievedPath = path;
		recieved = new byte[1024];
		in.read(recieved);
		return true;
	}
	
	public Server clone() {
		return this;
	}
}

