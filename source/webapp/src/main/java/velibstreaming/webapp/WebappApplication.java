package velibstreaming.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import velibstreaming.properties.StreamProperties;

@SpringBootApplication
public class WebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	}

	@Bean
	@Scope("singleton")
	public StationService stationServiceSingleton(){
		return new StationService();
	}

	@Bean
	@Scope("singleton")
	public StreamProperties streamProperties(){
		return StreamProperties.getInstance();
	}
}
