package fr.funetdelire.webftp.server;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
public class ServerController {

	@Autowired
	private Map<String, Server> servers;

	@PutMapping(path = "{alias}", consumes = "application/json")
	public void addServer(@RequestBody @Validated Server server, @PathVariable String alias) {
		servers.put(alias, server);
	}

	@GetMapping(path = "{alias}", produces = "application/json")
	public Server getServer(@PathVariable String alias) {
		return servers.get(alias);
	}
	
	@GetMapping(produces = "application/json")
	public Map<String, Server> getServers() {
		return servers;
	}
}
