package pain.flopbox;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import pain.flopbox.security.AuthContainerRequestFilter;
import pain.flopbox.security.user.ConfigurationException;
import pain.flopbox.security.user.UserConfiguration;
import pain.flopbox.security.user.UserConfigurationFactory;


public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in pain.flopbox package
        final ResourceConfig rc = new ResourceConfig().packages("pain.flopbox.resources");
        rc.register(RolesAllowedDynamicFeature.class);
        rc.register(AuthContainerRequestFilter.class);
        rc.register(JsonBindingFeature.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ConfigurationException {
    	if (args.length != 1) {
			System.exit(1);
		}
    	
		File configFile = new File(args[0]);
    	UserConfiguration config = UserConfigurationFactory.createServerConfig(configFile);
		UserConfiguration.setInstance(config);
		startServer();
    }
}

