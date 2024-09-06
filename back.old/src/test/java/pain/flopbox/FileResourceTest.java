package pain.flopbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pain.flopbox.ftp.ServerSave;
import pain.flopbox.security.user.UserConfiguration;

public class FileResourceTest {
	static HttpServer server;
	static WebTarget target;
	static ServerMock ftpServer;
	
	static String username = "thierry";
	static String password = "jadorelecole";
	
	@BeforeClass
	public static void setup() {
		server = Main.startServer();
		UserConfiguration.getInstance().addUser(username, password);
		
		Client client = ClientBuilder.newClient();
		HttpAuthenticationFeature auth = HttpAuthenticationFeature.basic(username, password);
		client.register(auth);
		target = client.target("http://localhost:8080");
		
		ftpServer = new ServerMock();
		ServerSave.getInstance().addServer("fun", ftpServer);
	}
	
	@AfterClass
	public static void setdown() {
		server.shutdownNow();
		UserConfiguration.setInstance(null);
		ServerSave.getInstance().removeServer("fun");
	}
	
	@Test
	public void sendBinary() throws URISyntaxException {
		Random rng = new Random();
		byte[] garbage = new byte[1024];
		
		rng.nextBytes(garbage);
		
		InputStream in = new ByteArrayInputStream(garbage);
		
		Response resp = target.path("/fun/path/to/file").request().post(Entity.entity(in, MediaType.APPLICATION_OCTET_STREAM));
		assertEquals(201, resp.getStatus());
		assertTrue(Arrays.equals(garbage, ftpServer.recieved));
		assertEquals("path/to/file", ftpServer.recievedPath);
		assertEquals("anonymous", ftpServer.recievedUsername);
		assertEquals("anonymous", ftpServer.recievedPassword);
		assertEquals(target.getUri() + "/fun/path/to/file", resp.getHeaderString("Location"));
	}
	
	@Test
	public void recieveBinary() throws URISyntaxException, IOException {
		Response resp = target.path("/fun/path/to/file2/3").request().get();
		InputStream r = resp.readEntity(InputStream.class);
		byte[] recieved = new byte[1024];
		r.read(recieved);
		assertEquals(200, resp.getStatus());
		assertEquals("path/to/file2/3", ftpServer.recievedPath);
		assertTrue(Arrays.equals(recieved, ftpServer.sent));
		assertEquals("anonymous", ftpServer.recievedUsername);
		assertEquals("anonymous", ftpServer.recievedPassword);
		
		resp = target.path("/fun/forbidden").request().get();
		assertEquals(404, resp.getStatus());
	}
	
	@Test
	public void renameFile() {
		Form f = new Form();
		f.param("newName", "delire");
		Response resp = target.path("/fun/path/to/filerfzf/3").request().put(Entity.entity(f,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertEquals(204, resp.getStatus());
		assertEquals("path/to/filerfzf/3", ftpServer.renameFrom);
		assertEquals("anonymous", ftpServer.recievedUsername);
		assertEquals("anonymous", ftpServer.recievedPassword);
		
		resp = target.path("/fun/forbidden").request().put(Entity.entity(f,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertEquals(404, resp.getStatus());
	}
	
	@Test
	public void mkDir() {
		Response resp = target.path("/fun/path/to/filerfzf/dir").request().post(Entity.text(""));
		
		assertEquals(201, resp.getStatus());
		assertEquals("path/to/filerfzf/dir", ftpServer.recievedPath);
		assertEquals("mkdir", ftpServer.pathMethod);
		assertEquals("anonymous", ftpServer.recievedUsername);
		assertEquals("anonymous", ftpServer.recievedPassword);
		
		resp = target.path("/fun/forbidden").request().post(Entity.text(""));
		assertEquals(403, resp.getStatus());
	}
	
	@Test
	public void delete() {
		Response resp = target.path("/fun/path/to/filerfzf/dir").request().delete();
		
		assertEquals(200, resp.getStatus());
		assertEquals("path/to/filerfzf/dir", ftpServer.recievedPath);
		assertEquals("delete", ftpServer.pathMethod);
		assertEquals("anonymous", ftpServer.recievedUsername);
		assertEquals("anonymous", ftpServer.recievedPassword);
		
		resp = target.path("/fun/forbidden").request().delete();
		assertEquals(404, resp.getStatus());
	}
	
	@Test
	public void list() {
		Response resp = target.path("/fun/path/to/filerfzf/dir").request().accept(MediaType.APPLICATION_JSON).get();
		
		JsonArray files = resp.readEntity(JsonArray.class);
		assertEquals(200, resp.getStatus());
		
		JsonObject one = files.get(0).asJsonObject();
		JsonObject two = files.get(1).asJsonObject();
		
		assertEquals("http://localhost:8080/fun/path/to/filerfzf/dir/fileone", one.getString("link"));
		assertEquals("http://localhost:8080/fun/path/to/filerfzf/dir/filetwo", two.getString("link"));
		assertEquals("anonymous", ftpServer.recievedUsername);
		assertEquals("anonymous", ftpServer.recievedPassword);
		
		resp = target.path("/fun/notfound").request().accept(MediaType.APPLICATION_JSON).get();
		assertEquals(404, resp.getStatus());
	}
	
	@Test
	public void badlog() {
		Response resp = target.path("/fun/path/to/filerfzf/dir").request().accept(MediaType.APPLICATION_JSON).header("X-Ftp-Username", "badlog").get();
		assertEquals(401, resp.getStatus());
	}
}
