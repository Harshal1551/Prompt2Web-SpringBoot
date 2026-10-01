package com.prompt2web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Prompt2webBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(Prompt2webBackendApplication.class, args);
		System.err.println("----------------------------");
	}

}
