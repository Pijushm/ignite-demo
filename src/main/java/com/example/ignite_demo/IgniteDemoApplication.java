package com.example.ignite_demo;

import java.lang.management.ManagementFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IgniteDemoApplication {

	public static void main(String[] args) {
		System.out.println("JVM Args: " + ManagementFactory.getRuntimeMXBean().getInputArguments());
		SpringApplication.run(IgniteDemoApplication.class, args);
	}

}
