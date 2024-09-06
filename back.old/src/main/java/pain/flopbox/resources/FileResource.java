package pain.flopbox.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.commons.net.ftp.FTPFile;
import org.glassfish.grizzly.http.util.URLDecoder;

import jakarta.annotation.security.RolesAllowed;
import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import pain.flopbox.exception.ServerAuthException;
import pain.flopbox.ftp.Server;
import pain.flopbox.ftp.ServerSave;

/**
 * File resource, represents a file on the server referred by alias.
 * (exposed at "{alias}/{path}") 
 * @author Enzo Pain
 */
@jakarta.ws.rs.Path("{alias}/{path: .*}")
public class FileResource {
	@DefaultValue("anonymous")
	@HeaderParam("X-Ftp-Username")
	private String user;
	@DefaultValue("anonymous")
	@HeaderParam("X-Ftp-Password")
	private String password;
	
	@PathParam("alias")
	private String alias;
	@PathParam("path")
	private String path;
	
	@Context
	private UriInfo contextUri;
	
	private void connectAndLog(Server s) throws IOException {
		//s.disconnect();
		s.connect();
		if (!s.login(user, password)) {
			throw new ServerAuthException();
		}
		if (!s.setup()) {
			System.err.println("Error while setuping ftp server");
			throw new ServerErrorException(500);
		}
	}
	
	/**
	 * Download the file of the given path from the FTP server.
	 * @return The HTTP response.
	 * Ok if the file is downloaded, Not Found if the file doesn't exists or if the directory is empty, Internal Error if an IOException occured.
	 */
	@GET
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile() {
		Server s = ServerSave.getInstance().getServer(alias);
		try {
			connectAndLog(s);
			InputStream in = s.retrFile(path);
			
			if (in != null) {
				Response r = Response.ok(in).build();
				s.disconnect();
				return r;
			}
			else {
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException(500);
		}
	}
	
	/**
	 * Show the file informations of the given path from the FTP server, or if it's a directory, list it's content.
	 * @return The HTTP response.
	 * Ok if the file is downloaded, Not Found if the file doesn't exists or if the directory is empty, Internal Error if an IOException occured.
	 * @throws URISyntaxException 
	 */
	@GET
	@RolesAllowed("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFileJson() throws URISyntaxException {
		Server s = ServerSave.getInstance().getServer(alias);
		try {
			connectAndLog(s);
			
			path = URLDecoder.decode(path);
			FTPFile[] files = s.listFiles(path);
			s.disconnect();
			
			if (files.length != 0) {
				Stream<FTPFile> filesStream = Arrays.stream(files);
				return Response.ok(
						filesStream.map( f -> {
							try {
								if (f.getName().charAt(0) != '/') {
									URI fileUri = URI.create(contextUri.getRequestUri() + "/" + URLEncoder.encode(f.getName(), "UTF-8"));
									return Json.createObjectBuilder()
											.add("link", fileUri.toString())
											.build();
								}
								else {
									return Json.createObjectBuilder()
											.add("date", String.valueOf(f.getTimestamp().getTimeInMillis()))
											.build();
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
								throw new ServerErrorException(500);
							}
							
						}).toArray()).build();
			}
			else {
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException(500);
		}
	}
	
	/**
	 * Delete the file or directory of the given path.
	 * @return The HTTP response.
	 * Ok if the file was deleted, Not Found if the file doesn't exists or if the directory is not empty, Internal Error if an IOException occured.
	 */
	@DELETE
	@RolesAllowed("user")
	public Response removeFile() {
		Server s = ServerSave.getInstance().getServer(alias);
		try {
			connectAndLog(s);
			if (s.delFile(path) || s.delDir(path)) {
				s.disconnect();
				return Response.ok().build();
			}
			else {
				s.disconnect();
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException(500);
		}
	}
	
	/**
	 * Rename the file or directory of the given path.
	 * @return The HTTP response.
	 * Ok if the file was renamed, Not Found if the file doesn't exists, Internal Error if an IOException occured.
	 */
	@PUT
	@RolesAllowed("user")
	public Response renameFile(@FormParam("newName") String newName) {
		Server s = ServerSave.getInstance().getServer(alias);
		try {
			connectAndLog(s);
			
			Path curr = Path.of("/");
			Iterator<Path> it = Path.of("/" + newName).getParent().iterator();
			while (it.hasNext()) {
				curr = curr.resolve(it.next());
				s.makeDirectory(curr.toString());
			}
			if (s.renameFile(path, newName)) {
				s.disconnect();
				return Response.noContent().build();
			}
			else {
				s.disconnect();
				return Response.status(Status.NOT_FOUND).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	/**
	 * Adds a directory with the given path.
	 * @return The HTTP response.
	 * Created if the directory was created, Forbidden if the file exists, Internal Error if an IOException occured.
	 */
	@POST
	@RolesAllowed("user")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response addDir() {
		Server s = ServerSave.getInstance().getServer(alias);
		try {
			connectAndLog(s);
			if (s.makeDirectory(path)) {
				s.disconnect();
				return Response.created(contextUri.getRequestUri()).build();
			} else {
				s.disconnect();
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException(500);
		}
	}
	
	/**
	 * Send the file in the request to the FTP server with the given path.
	 * @param inFile The 
	 * @return The HTTP response.
	 * Created if the file was sent, Forbidden if the file exists, Internal Error if an IOException occured. 
	 */
	@POST
	@RolesAllowed("user")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response sendFile(InputStream inFile) {
		Server s = ServerSave.getInstance().getServer(alias);
		try {
			connectAndLog(s);
			Path curr = Path.of("/");
			Path parent = Path.of("/"+path).getParent();
			Iterator<Path> it = parent.iterator();
			while (it.hasNext()) {
				curr = curr.resolve(it.next());
				s.makeDirectory(curr.toString());
			}
			if (s.storFile(path, inFile)) {
				s.disconnect();
				return Response.created(contextUri.getRequestUri()).build();
			} else {
				s.disconnect();
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerErrorException(500);
		}
	}
}
