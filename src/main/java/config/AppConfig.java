package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import server_utilities.DefaultProperties;

@Configuration
@ComponentScan(basePackages = { "config", "service", "http_controllers", "database_entities", "database_entities.utilities" })
@Import(value = { MongoSpringConfig.class, WebConfig.class })
public class AppConfig {

	/**
	 * Application Properties bean
	 */
	@Bean
	public DefaultProperties defaultProperties() {
		return new DefaultProperties();
	}
}
