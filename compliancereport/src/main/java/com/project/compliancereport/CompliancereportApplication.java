package com.project.compliancereport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CompliancereportApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompliancereportApplication.class, args);
	}

}
