package pain.flopbox.security;

import java.util.Base64;

import javax.naming.AuthenticationException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import pain.flopbox.security.user.UserConfiguration;

@Provider
@PreMatching
/**
 * Filter for the Basic authentication.
 * @author Enzo Pain
 */
public class AuthContainerRequestFilter implements ContainerRequestFilter {

	/**
	 * Authenticate the given user with the HTTP Authorization header.
	 * @param auth The HTTP Authorization header
	 * @return The security context of the authenticated user
	 * @throws AuthenticationException If the credentials are incorrect
	 */
	private SecurityContext authUser(String auth) throws AuthenticationException {
		if (auth == null) {
			throw new AuthenticationException();
		}
		
		String[] parts = auth.split(" +");
		if (parts.length != 2 || !"Basic".equals(parts[0])) {
			throw new AuthenticationException();
		}
		
		String credentials = new String(Base64.getDecoder().decode(parts[1]));
		String[] userAndPass = credentials.split(":");
		if (userAndPass.length != 2) {
			throw new AuthenticationException();
		}
		if (!UserConfiguration.getInstance().authenticate(userAndPass[0], userAndPass[1])) {
			throw new AuthenticationException(); 
		}
		return new SimpleUserSecurityContext(userAndPass[0]);
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) {
		String auth = requestContext.getHeaderString("Authorization");
		try {
			requestContext.setSecurityContext(authUser(auth));
		} catch (AuthenticationException e) {
			Response unauth = Response
					.status(Status.UNAUTHORIZED)
					.header("WWW-Authenticate", "Basic realm=\"flopbox\"")
					.build();
			requestContext.abortWith(unauth);
		}
	}
}
