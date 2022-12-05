package com.entando.example.e71springbootms;

import com.entando.en7.En7ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class E71SpringBootMsApplication {

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(E71SpringBootMsApplication.class);
        app.setApplicationContextFactory(new En7ApplicationContextFactory()); // added for en7
        app.run(args);
		//SpringApplication.run(E71SpringBootMsApplication.class, args);
	}

}
