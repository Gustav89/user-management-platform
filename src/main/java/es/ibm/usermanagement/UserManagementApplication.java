package es.ibm.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "es.ibm.usermanagement")
@EnableAsync
@EnableCaching
public class UserManagementApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}

}
