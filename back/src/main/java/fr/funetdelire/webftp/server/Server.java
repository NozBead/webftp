package fr.funetdelire.webftp.server;

import java.io.IOException;
import java.net.URI;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class Server implements Cloneable {

	private String root;
	@NotBlank
	private String host;
	@NotBlank
	private String login;
	@NotBlank
	private String password;
	@Positive
	private int port;
	private boolean passive;

	@FunctionalInterface
	public interface FileFunction<R> {
		R apply(String path) throws IOException;
	}

	@FunctionalInterface
	public interface FileBiFunction<T, R> {
		R apply(String path, T t) throws IOException;
	}

	public Server() {
		this.port = 21;
		this.root = "";
		this.passive = true;
	}

	public Server(String root, String host, int port, boolean passive) {
		this.root = root;
		this.host = host;
		this.port = port;
		this.passive = passive;
	}

	public Server(String root, String host) {
		this(root, host, 21, true);
	}

	public Server(URI uri, boolean passive) {
		this(uri.getPath(), uri.getHost(), uri.getPort(), passive);
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	private String addRoot(String path) {
		return root + "/" + path;
	}

	public <T> T doFileAction(String path, FileFunction<T> action) throws IOException {
		return action.apply(addRoot(path));
	}

	public <T, R> R doFileAction(String path, T t, FileBiFunction<T, R> action) throws IOException {
		return action.apply(addRoot(path), t);
	}

	public Server clone() {
		return new Server(root, host, port, passive);
	}
}
