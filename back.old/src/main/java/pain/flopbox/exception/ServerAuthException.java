package pain.flopbox.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Exception caused by a bad authentication to a ftp server.
 * @author Enzo Pain
 */
public class ServerAuthException extends WebApplicationException{
	public ServerAuthException() {
	    super(Response.status(Status.UNAUTHORIZED).build());
	 }
}
