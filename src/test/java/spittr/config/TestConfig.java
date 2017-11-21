package spittr.config;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import spittr.data.SpitterRepository;
import spittr.data.SpittleRepository;
import spittr.web.SpittrWebAppExceptionHandler;

@Configuration
public class TestConfig {

	@Bean
	public SpitterRepository spitterRpository() throws IOException {
		return mock(SpitterRepository.class);
	}

	@Bean
	public SpittleRepository spittleRpository() throws IOException {
		return mock(SpittleRepository.class);
	}
	
	@Bean 
	public SpittrWebAppExceptionHandler spittrWebAppExceptionHandler() {
		return new SpittrWebAppExceptionHandler();
	}
}
