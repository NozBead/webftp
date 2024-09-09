package fr.funetdelire.webftp.server;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class ConnectionPool {
	
	private Map<Server, FTPClient> clients = new HashMap<>();
	
	public boolean isConnected(FTPClient client) {
		try {
			int result = client.noop();
			return result == 200;
		} catch (IOException e) {
			return false;
		}
	}

	public FTPClient getConnection(Server server) throws IOException {
		FTPClient client = clients.get(server);
		if (client == null || !isConnected(client)) {
			client = new FTPClient();
			client.connect(server.getHost());
			client.login(server.getLogin(), server.getPassword());
			if (server.isPassive()) {
				client.enterLocalPassiveMode();
			}
			client.setControlKeepAliveTimeout(Duration.ofMinutes(1));
			client.setFileType(FTP.BINARY_FILE_TYPE);
			clients.put(server, client);
		}
	
		return client;
	}
}
