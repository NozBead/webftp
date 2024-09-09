package fr.funetdelire.webftp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import fr.funetdelire.webftp.server.ConnectionPool;
import fr.funetdelire.webftp.server.Server;

@SpringBootApplication
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "fr.funetdelire.webftp")
public class WebftpApplication {
	
	@Bean
	public Map<String, Server> serverSave() {
		return new HashMap<>();
	}
	
	@Bean
	public ConnectionPool pool() {
		return new ConnectionPool();
	}

	public static void main(String[] args) {
		SpringApplication.run(WebftpApplication.class, args);
	}
}
