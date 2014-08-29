package config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import server_utilities.DefaultProperties;

@Configuration
@ComponentScan(basePackages = { "config", "service", "http_controllers", "database_entities", "database_entities.utilities" })
@Import(value = { MongoSpringConfig.class, WebConfig.class })
@EnableCaching
public class AppConfig {

	/**
	 * Application Properties bean
	 */
	@Bean
	public DefaultProperties tradeRoomProperties() {
		return new DefaultProperties();
	}
	
	/**
	 * Simple Cache Manager (for now)
	 * @return
	 */
	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(new ConcurrentMapCache("default")));
		return manager;
	}
	
}
