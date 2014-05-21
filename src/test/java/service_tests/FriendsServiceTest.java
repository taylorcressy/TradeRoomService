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
import service.FriendsService;
import service.ServerMessage;
import database_entities.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class FriendsServiceTest {

	
	private static final Logger log = LoggerFactory.getLogger("service-test-logger");
	
	private User primaryUser, otherUser;
	
	@Autowired private UserRepository userRepo;
	@Autowired private FriendsService friendService;
	
	private void setup() {
		this.primaryUser = userRepo.findOneByUsername("rayred");
		assertNotNull(primaryUser);
		this.otherUser = userRepo.findOneByUsername("upshutdown");
	}
	
	
	@Test
	public void testRetrievingFriends() {
		setup();
		
		ServerMessage message = friendService.getAllFriends(this.primaryUser);
		
		assertNotNull(message);
		log.debug(message.toString());
	}
	
	@Test
	public void testSendingFriendRequest() {
		setup();
		
		ServerMessage message = friendService.sendFriendRequest(this.primaryUser, "upshutdown");
		
		assertNotNull(message);
		log.debug("Message: " + message);
	}
	
	@Test
	public void testAcceptingFriendRequest() {
		setup();
		
		ServerMessage message = friendService.acceptFriendRequest(this.otherUser, "rayred");
		assertNotNull(message);
		log.debug("Message: " + message);
	}
}
