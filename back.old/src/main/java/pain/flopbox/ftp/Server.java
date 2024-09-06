package pain.flopbox.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * FTP Server representation.
 * @author Enzo Pain
 */
public class Server implements Cloneable {
	private transient FTPClient client;
	
	private String root;
	private String host;
	private int port;
	private boolean passive;
	
	public Server(String root, String host, int port, boolean passive) {
		this.root = root;
		this.host = host;
		this.port = port == -1 ? 21 : port;
		this.passive = passive;
	}
	
	public Server(String root, String host) {
		this(root, host, 21, true);
	}
	
	public Server(URI uri, boolean passive) {
		this(uri.getPath(), uri.getHost(), uri.getPort(), passive);
	}
	
	public FTPClient getClient() {
		return client;
	}
	
	public boolean isPassive() {
		return passive;
	}

	public String getRoot() {
		return root;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void connect() throws IOException {
		client = new FTPClient();
		client.connect(host, port);
	}
	
	public void disconnect() throws IOException {
		client.logout();
		client.disconnect();
	}
	
	public boolean setup() throws IOException {
		if (passive) {
			client.enterLocalPassiveMode();
		}
		return client.setFileType(FTP.BINARY_FILE_TYPE);
	}
	
	public boolean login(String username, String password) throws IOException {
		return client.login(username, password);
	}
	
	private String wrapPath(String path) {
		return root + "/" + path;
	}
	
	public InputStream retrFile(String path) throws IOException {
		return client.retrieveFileStream(wrapPath(path));
	}
	
	public boolean delFile(String path) throws IOException {
		return client.deleteFile(wrapPath(path));
	}

	public boolean delDir(String path) throws IOException {
		return client.removeDirectory(wrapPath(path));
	}
	
	public boolean renameFile(String path, String newName) throws IOException {
		return client.rename(wrapPath(path), wrapPath(newName));
	}
	
	public boolean storFile(String path, InputStream in) throws IOException {
		return client.storeFile(wrapPath(path), in);
	}
	
	public boolean makeDirectory(String path) throws IOException {
		return client.makeDirectory(wrapPath(path));
	}
	
	public FTPFile[] listFiles(String path) throws IOException {
		return client.listFiles(wrapPath(path));
	}
	
	public Server clone() {
		return new Server(root, host, port, passive);
	}
}
