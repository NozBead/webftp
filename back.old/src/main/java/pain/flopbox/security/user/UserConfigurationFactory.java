package pain.flopbox.security.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * UserConfigration creation.
 * @author Enzo Pain
 */
public class UserConfigurationFactory {
	/**
	 * Create a user configuration based on the given file.
	 * @param file The file describing the users
	 * @return The Configuration
	 * @throws IOException If an error appeared during the file reading
	 * @throws ConfigurationException If the file syntax is incorrect
	 */
	public static UserConfiguration createServerConfig(File file) throws IOException, ConfigurationException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		UserConfiguration config = new UserConfiguration();
		
		String line = reader.readLine();
		while(line != null) {
			String[] parts = line.split("( |\t)+");
			if (parts.length != 2) {
				reader.close();
				throw new ConfigurationException(line);
			}
			config.addUser(parts[0], parts[1]);
			line = reader.readLine();
		}
		
		reader.close();
		return config;
	}
}
