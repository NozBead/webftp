package pain.flopbox.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Exception caused by a request on a non existing ftp server.
 * @author Enzo Pain
 */
public class ServerNotFoundException extends WebApplicationException {
	public ServerNotFoundException() {
	    super(Response.status(Status.NOT_FOUND).entity("Server not found").type("text/plain").build());
	}
}
