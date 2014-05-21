/**
 * Main startup point for TradeRoomService. Calls Spring's main function and subsequently
 * looks up "Controllers" in this package to listen to incoming RESTful requests.
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @date April 15, 2014
 */

package http_controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import config.AppConfig;

@Configuration
@Import(value={AppConfig.class})
@EnableAutoConfiguration
public class MainWebService {
	
	public static void main(String [] args) {	
		SpringApplication.run(MainWebService.class, args);
	}
}
