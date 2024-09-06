package pain.flopbox.security.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the configuration of the users credentials.
 * @author Enzo Pain
 */
public class UserConfiguration {
	private static UserConfiguration instance;
	
	Map<String, String> passwords;
	
	public UserConfiguration() {
		passwords = new HashMap<>();
	}
	
	public static UserConfiguration getInstance() {
		if (instance == null) {
			instance = new UserConfiguration();
		}
		return instance;
	}
	
	public static void setInstance(UserConfiguration config) {
		instance = config;
	}
	
	/**
	 * Check given credentials.
	 * @param username The username
	 * @param password The password
	 * @return true if correct, false otherwise
	 */
	public boolean authenticate(String username, String password) {
		String passwd = passwords.get(username);
		return passwd != null && password.equals(passwd);
	}
	
	/**
	 * Add credentials to configuration.
	 * @param username The username
	 * @param password The password
	 * @param root La racine de l'utilisateur
	 */
	public void addUser(String username, String password) {
		passwords.put(username, password);
	}
}
