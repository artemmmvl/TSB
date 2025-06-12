package com.example.tsb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TsbApplication {

	public static void main(String[] args) {
		SpringApplication.run(TsbApplication.class, args);
	}

}
