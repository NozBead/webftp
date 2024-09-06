package pain.flopbox.ftp;

import java.util.HashMap;
import java.util.Map;

import pain.flopbox.exception.ServerNotFoundException;

/**
 * Memory persistence of the ftp servers informations. 
 * @author Enzo Pain
 */
public class ServerSave {
	private static final ServerSave instance = new ServerSave();
	
	private Map<String, Server> servers;
	
	public ServerSave() {
		servers = new HashMap<>();
	}
	
	public static ServerSave getInstance() {
		return instance;
	}
	
	public boolean addServer(String alias, Server s) {
		if (servers.containsKey(alias)) {
			return false;
		}
		
		servers.put(alias, s);
		return true;
	}
	
	public void removeServer(String alias) {
		if (servers.remove(alias) == null) {
			throw new ServerNotFoundException();
		}
	}
	
	public Server getServer(String alias) {
		Server s = servers.get(alias);
		
		if (s == null) {
			throw new ServerNotFoundException();
		}
		
		return s.clone();
	}
	
	public Map<String, Server> getServers() {
		return servers;
	}
}
