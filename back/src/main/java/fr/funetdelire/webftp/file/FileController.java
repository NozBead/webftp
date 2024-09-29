package fr.funetdelire.webftp.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.funetdelire.webftp.server.ConnectionPool;
import fr.funetdelire.webftp.server.Server;

@RestController
@CrossOrigin
@RequestMapping("/server/{alias}/{*path}")
public class FileController {

	@Autowired
	private Map<String, Server> servers;

	@Autowired
	private ConnectionPool connections;

	@GetMapping(produces = "application/octet-stream")
	public ResponseEntity<Void> getFile(OutputStream out, @PathVariable String path, @PathVariable String alias)
			throws IOException {
		Server s = servers.get(alias);
		FTPClient client = connections.getConnection(s);
		InputStream in = s.doFileAction(path, client::retrieveFileStream);
		if (in != null) {
			in.transferTo(out);
			client.completePendingCommand();
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping(produces = "application/json")
	public ResponseEntity<FTPFile[]> getFileStats(@PathVariable String path, @PathVariable String alias)
			throws IOException {
		Server s = servers.get(alias);
		FTPClient client = connections.getConnection(s);
		path = URLDecoder.decode(path, Charset.defaultCharset());
		FTPFile[] files = s.doFileAction(path, client::listFiles);
		if (files.length != 0) {
			return ResponseEntity.ok(files);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping
	public ResponseEntity<Void> removeFile(@PathVariable String path, @PathVariable String alias) throws IOException {
		Server s = servers.get(alias);
		FTPClient client = connections.getConnection(s);
		if (s.doFileAction(path, client::deleteFile) || s.doFileAction(path, client::removeDirectory)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(consumes = "application/json")
	public ResponseEntity<Void> renameFile(@PathVariable String path, @PathVariable String alias,
			@RequestBody String newName) throws IOException {
		Server s = servers.get(alias);
		FTPClient client = connections.getConnection(s);
		try {
			Path curr = Path.of("/");
			Iterator<Path> it = Path.of("/" + newName).getParent().iterator();
			while (it.hasNext()) {
				curr = curr.resolve(it.next());
				s.doFileAction(curr.toString(), client::makeDirectory);
			}
			if (s.doFileAction(path, newName, client::rename)) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@PutMapping(consumes = "application/json")
	public ResponseEntity<Void> addDir(@PathVariable String path, @PathVariable String alias) throws IOException {
		Server s = servers.get(alias);
		FTPClient client = connections.getConnection(s);
		if (s.doFileAction(path, client::makeDirectory)) {
			return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri()).build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@PutMapping(consumes = "application/octet-stream")
	public ResponseEntity<Void> sendFile(@PathVariable String path, @PathVariable String alias, InputStream inFile)
			throws IOException {
		Server s = servers.get(alias);
		FTPClient client = connections.getConnection(s);

		Path curr = Path.of("/");
		Path parent = Path.of("/" + path).getParent();
		Iterator<Path> it = parent.iterator();
		while (it.hasNext()) {
			curr = curr.resolve(it.next());
			s.doFileAction(curr.toString(), client::makeDirectory);
		}
		if (s.doFileAction(path, inFile, client::storeFile)) {
			return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri()).build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

	}
}
