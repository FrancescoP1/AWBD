package com.fmi.eduhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
		"com.fmi.eduhub.entity",
		"com.fmi.eduhub.authentication.jwtToken"})
public class EduhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduhubApplication.class, args);
	}

}
