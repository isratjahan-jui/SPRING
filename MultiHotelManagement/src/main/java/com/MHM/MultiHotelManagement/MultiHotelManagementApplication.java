package com.MHM.MultiHotelManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultiHotelManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiHotelManagementApplication.class, args);
	}

}
