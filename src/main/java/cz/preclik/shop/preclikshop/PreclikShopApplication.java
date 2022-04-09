package cz.preclik.shop.preclikshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PreclikShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreclikShopApplication.class, args);
	}

}
