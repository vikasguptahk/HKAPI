package com.example.hkProxyServer;

import com.example.hkProxyServer.mitm.ProxyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HkProxyServerApplication {
	@Autowired
	private ProxyServer proxyServer;

	public static void main(String[] args) {
		SpringApplication.run(HkProxyServerApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(){
		return args -> {
			proxyServer.start(8042);
		};
	}

}
