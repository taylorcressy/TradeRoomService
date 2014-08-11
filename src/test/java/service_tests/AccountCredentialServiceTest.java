package service_tests;


import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.AppConfig;
import database_entities.User;
import service.AccountCredentialService;
import service.ServerMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class AccountCredentialServiceTest {

	private static final Logger log = LoggerFactory.getLogger("service-test-logger");
	
	@Autowired AccountCredentialService service;
	
	private User helperUser;
	
	//This function is a proxy method for obtaining the session object at the Controller level.
		//i.e. we create a user already present in the DB and pass it to the service level as if it were the Controller
	private void setup() {
		this.helperUser = new User();
		this.helperUser.setEmail("upshutdown@gmail.com");
		this.helperUser.setUsername("rayred");
	}
	
	@Test
	public void testRegister() {
		ServerMessage message = service.registerNewUser("rayred", "upshutdown@gmail.com", "ThisIsARealPassword1", "Taylor", "Cressy", "fake");
		assertNotNull(message);
		log.debug("Response: " + message);
	}
	
	@Test
	public void testLogin() {
		ServerMessage message = service.login("upshutdown@gmail.com", "ThisIsARealPassword1");
		assertNotNull(message);
		log.debug("Retrieved Message: " + message);
		User user = (User) message.getData();
		assertTrue(user instanceof User);
		log.debug("Successfully passed User instance from login service");
	}
	
	@Test
	public void testUpdatePreferences() {
		setup();
		ServerMessage message = service.updateUserPreferences(this.helperUser, "newPassword12", "Taylor", "Cressy", "01-01-1991");
		assertNotNull(message);
		log.debug("Retrieved Message: " + message);
	}
	
	@Test
	public void testUpdateAddress() {
		setup();
		ServerMessage message = service.updateUserAddress(this.helperUser, "Orion", "1234", "91406", "United States", "Van Nuys", "Los Angeles", "Some Code");
		assertNotNull(message);
		log.debug("Retrieved Message: " + message);
	}
}
