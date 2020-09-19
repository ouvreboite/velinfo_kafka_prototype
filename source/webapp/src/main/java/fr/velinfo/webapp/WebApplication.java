package fr.velinfo.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import fr.velinfo.properties.ConnectionConfiguration;

@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@Bean
	@Scope("singleton")
	public StationService stationServiceSingleton(){
		return new StationService();
	}

	@Bean
	@Scope("singleton")
	public AvroJsonMapper avroJsonMapper(){ return new AvroJsonMapper(); }
}
