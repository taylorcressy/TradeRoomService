package database_tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.AppConfig;
import database_entities.FriendRequest.FriendRequestStatus;
import database_entities.User;
import database_entities.UserRepository;
import database_entities.exceptions.DetailedDuplicateKeyException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
public class TestFriendRequests {

	private final Logger log = LoggerFactory.getLogger("database-test-logger"); // LoggerFactory.getLogger(TestUserCollection.class);

	private User helperUser, recipUser;

	@Autowired
	UserRepository userRepo;
	
	private void setup() {
		
		this.helperUser = new User("upshutdown","taylorcressy@gmail.com", null, null, null, null, null, new Date());
		this.recipUser = new User("rayred", "upshutdown@gmail.com", null, null, null, null, null, new Date());
	}
	
	private void insertHelperUsers() {
		User user;
		try {
			user = userRepo.save(this.helperUser);
			assertNotNull(user);
			user = userRepo.save(this.recipUser);
			assertNotNull(user);
		}
		catch(DetailedDuplicateKeyException e) {
			log.debug("DUPLICATED: " + e.getDuplicatedIndex());
			return;
		}
	}
	
	private void deleteHelperUsers() {
		boolean helper = userRepo.deleteUserByUsername(this.helperUser.getUsername());
		boolean recip = userRepo.deleteUserByUsername(this.recipUser.getUsername());
		
		if(!helper || !recip)
			log.debug("failed to delete!");
	}
	
	@Test
	public void testSendingFriendRequest() {
		insertHelperUsers();
		
		boolean success = userRepo.sendFriendRequest(this.helperUser, this.recipUser.getUsername());
		
		//Update recipient user (as would happen within the sessions environment)
		recipUser = userRepo.findOneByUsername(recipUser.getUsername());
		
		assertTrue(success);
		
		//deleteHelperUsers();
	}
	
	@Test
	public void testAcceptingFriendRequest() {
		setup();
		deleteHelperUsers();
		
		testSendingFriendRequest();
		boolean success = userRepo.acceptFriendRequest(this.recipUser, this.helperUser.getUsername());
		assertTrue(success);
		int index = -1;
		for(int i=0; i < recipUser.getFriendRequests().size(); i++) {
			if(recipUser.getFriendRequests().get(i).getSenderUsername().compareToIgnoreCase(this.helperUser.getUsername()) == 0)
				index = i;
		}
		assertFalse(index == -1);
		
		assertTrue(recipUser.getFriendRequests().get(index).getStatus() == FriendRequestStatus.ACCEPTED);
	}
	
	@Test
	public void testBlockingFriendRequest() {
		setup();
		deleteHelperUsers();
		
		testSendingFriendRequest();
		boolean success = userRepo.denyFriendRequest(this.recipUser, this.helperUser.getUsername());
		assertTrue(success);
		int index = -1;
		for(int i=0; i < recipUser.getFriendRequests().size(); i++) {
			if(recipUser.getFriendRequests().get(i).getSenderUsername().compareToIgnoreCase(this.helperUser.getUsername()) == 0)
				index = i;
		}
		assertFalse(index == -1);
		
		assertTrue(recipUser.getFriendRequests().get(index).getStatus() == FriendRequestStatus.DENIED);
	}
	
	@Test
	public void testDenyingFriendRequest() {
		setup();
		deleteHelperUsers();
		
		testSendingFriendRequest();
		boolean success = userRepo.denyFriendRequest(this.recipUser, this.helperUser.getUsername());
		assertTrue(success);
		int index = -1;
		for(int i=0; i < recipUser.getFriendRequests().size(); i++) {
			if(recipUser.getFriendRequests().get(i) != null) {
				if(recipUser.getFriendRequests().get(i).getSenderUsername().compareToIgnoreCase(this.helperUser.getUsername()) == 0)
					index = i;
			}
		}
		assertTrue(index == -1);		
	}
}
