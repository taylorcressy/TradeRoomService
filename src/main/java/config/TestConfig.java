/**
 * Configuration for unit tests. In essence, it simply excludes the WebConfig because there is not ServletContext
 * within the unit tests. Moreover, it excludes the component scan within the config file so we can explicitly
 * import the configurations we want.
 * 
 * @author Taylor Cressy
 * @Date 27 April, 2014
 * @version 1.0
 */

package config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import server_utilities.DefaultProperties;



@Configuration
@ComponentScan(basePackages = {"service", "database_entities", "database_entities.utilities" })
@Import(value = {MongoSpringConfig.class})
public class TestConfig {
	/**
	 * Application Properties bean to be intialized on startup
	 * 
	 */
	@Bean
	public DefaultProperties defaultProperties() {
		return new DefaultProperties();
	}
}
