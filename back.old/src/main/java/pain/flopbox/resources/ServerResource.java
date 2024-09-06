package pain.flopbox.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import pain.flopbox.ftp.Server;
import pain.flopbox.ftp.ServerSave;

/**
 * Server resource, represents an FTP server.
 * (exposed at "servers") 
 * @author Enzo Pain
 */
@Path("servers")
public class ServerResource {
	@Context
	private UriInfo contextUri;
	
	/**
	 * List all the saved servers.
	 * @return A Set of the saved server with their aliases
	 */
	@GET
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Server> getServers() {
		return ServerSave.getInstance().getServers();
	}
	
	/**
	 * Parse a string to create a FTP URI.
	 * @param urlStr The FTP server URL as a String
	 * @return The FTP server URI
	 */
	private URI getFtpUrl(String urlStr) {
		URI uri;
		try {
			String uriStr = urlStr;
			if (!urlStr.contains("//")) {
				// Add post-scheme '//' to avoid host taken as scheme
				uriStr = "//" + urlStr;
			}
			uri = new URI(uriStr);
			if (uri.getScheme() != null && !"ftp".equals(uri.getScheme())) {
				return null;
			}
			return uri;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	/**
	 * Adds a server.
	 * @param alias The name referring to this server
	 * @param urlStr The URL of the FTP server
	 * @return The server alias
	 * @throws URISyntaxException 
	 */
	@POST
	public Response addServer(	@FormParam("alias") String alias,
								@FormParam("url") String urlStr,
								@DefaultValue("true")
								@FormParam("passive") boolean passive) throws URISyntaxException {
		
		URI uri = getFtpUrl(urlStr);
		if (uri == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Server s = new Server(uri, passive);
		if (!ServerSave.getInstance().addServer(alias, s)) {
			return Response.status(Status.CONFLICT).build();
		}
		
		URI createdUri = new URI(contextUri.getRequestUri() + "/" + alias);
		return Response.created(createdUri).build();
	}
	
	/**
	 * Get the server settings (host, port, alias, data connection mode).
	 * @param alias Name of the server
	 * @return The server
	 */
	@Path("{alias}")
	@GET
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Server getServer(@PathParam("alias") String alias) {
		return ServerSave.getInstance().getServer(alias);
	}
	
	/**
	 * Delete the server.
	 * @param alias Name of the server
	 * @return The HTTP Response.
	 * OK if the server is deleted, Not Found if the server doesn't exists.
	 */
	@Path("{alias}")
	@DELETE
	@RolesAllowed("user")
	public Response removeServer(@PathParam("alias") String alias) {
		ServerSave.getInstance().removeServer(alias);
		return Response.ok().build();
	}
	
	/**
	 * Update the server.
	 * @param alias Name of the server
	 * @param newAlias The new name referring to this server
	 * @param newUrlStr The new URL of the FTP server
	 * @return The HTTP Response.
	 * OK if the server was updated, Not Found if the server doesn't exists.
	 * @throws URISyntaxException 
	 */
	@Path("{alias}")
	@PUT
	@RolesAllowed("user")
	public Response updateServer(	@PathParam("alias") String alias,
									@FormParam("alias") String newAlias,
									@FormParam("url") String newUrlStr,
									@DefaultValue("true")
									@FormParam("passive") boolean passive) throws URISyntaxException {
		removeServer(alias);
		addServer(newAlias, newUrlStr, passive);
		return Response.noContent().build();
	}
}
