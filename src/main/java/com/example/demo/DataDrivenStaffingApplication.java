package com.example.demo;

import com.example.demo.config.ScoringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ScoringProperties.class)

public class DataDrivenStaffingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataDrivenStaffingApplication.class, args);
	}

}
