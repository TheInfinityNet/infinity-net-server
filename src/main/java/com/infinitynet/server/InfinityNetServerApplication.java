package com.infinitynet.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InfinityNetServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfinityNetServerApplication.class, args);
	}

}
