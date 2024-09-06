package pain.flopbox.security;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

/**
 * Represents a simple authenticated user with the role "user".
 * @author Enzo Pain
 */
public class SimpleUserSecurityContext implements SecurityContext {
	private SimpleUser user;

	public SimpleUserSecurityContext(SimpleUser user) {
		this.user = user;
	}
	
	public SimpleUserSecurityContext(String username) {
		this.user = new SimpleUser(username);
	}

	@Override
	public Principal getUserPrincipal() {
		return user;
	}

	@Override
	public boolean isUserInRole(String role) {
		return "user".equals(role);
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public String getAuthenticationScheme() {
		return SecurityContext.BASIC_AUTH;
	}
	
	public class SimpleUser implements Principal {
		String name;

		public SimpleUser(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
	}
}
