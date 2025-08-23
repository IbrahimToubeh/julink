package com.example.julink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JulinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(JulinkApplication.class, args);
	}

}
