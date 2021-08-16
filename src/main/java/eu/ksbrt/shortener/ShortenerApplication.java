package eu.ksbrt.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// @SpringBootApplication
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortenerApplication.class, args);
	}

}
