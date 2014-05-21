/**
 * Test cases for the User Collections in MongoDB
 * 
 * @author Taylor Cressy
 */
package database_tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.AppConfig;
import database_entities.AccountPreference;
import database_entities.Address;
import database_entities.User;
import database_entities.UserRepository;
import database_entities.exceptions.DetailedDuplicateKeyException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class TestUserDatabaseHandling {

	private final Logger log = LoggerFactory.getLogger("database-test-logger");
	
	private User primaryUser, helperUser;
	
	
	@Autowired UserRepository userRepo;

	private void setup() {		
		AccountPreference pref = new AccountPreference("1234", "Taylor", "Cressy", null, new Address("Mary Lane", "1234",
				"KT1 1Ty", "England", "Norbiton", "London", "12345 - 12345"), "12345 - 12345", null);
		AccountPreference realisticRegisterPref = new AccountPreference("1234", "Taylor", "Cressy", null, null, null, null);
		
		this.primaryUser = new User("test","test@gmail.com", pref, null, null, null, null, new Date());
				
		this.helperUser = new User("test1", "test1@gmail.com", realisticRegisterPref, null, null, null, null, new Date());
	}
	
	private void insertHelperUser() {
		User user;
		try {
			user = userRepo.save(this.helperUser);
			assertNotNull(user);
			user = userRepo.save(this.primaryUser);
			assertNotNull(user);
		}
		catch(DetailedDuplicateKeyException e) {
			log.debug("DUPLICATED: " + e.getDuplicatedIndex());
			return;
		}
	}
	
	private void deleteHelperUser() {
		userRepo.delete(this.helperUser);
		userRepo.delete(this.primaryUser);
	}

	
	@Test
	public void testCrudUser() {
		setup();
		
		User user;
		try {
			user = userRepo.save(this.primaryUser);
			log.debug("CREATED: " + this.primaryUser);
			assertNotNull(user);
		}
		catch(DetailedDuplicateKeyException e) {
			log.debug("DUPLICATED: " + e.getDuplicatedIndex());
			return;
		}
						
		Page<User> pages = userRepo.findAll(new PageRequest(0, 10));
		assertNotNull(pages.getContent());		
		
		userRepo.delete(user);
	}
	
	@Test
	public void testFindingMultipleUsersByUsername() {
		setup();
		this.insertHelperUser();
		
		ArrayList<String> usernames = new ArrayList<String>();
		usernames.add("test");
		usernames.add("test1");		
		
		List<User> users = userRepo.findMultipleUsersByUsername(usernames);
				
		for(User user: users)
			log.debug("Found user: " + user);
		
		this.deleteHelperUser();
	}
	
	@Test
	public void testFindingMultipleUsersByEmail() {
		setup();
		this.insertHelperUser();
		
		ArrayList<String> emails = new ArrayList<String>();
		emails.add("test@gmail.com");
		emails.add("test1@gmail.com");		
		
		List<User> users = userRepo.findMultipleUsersByEmail(emails);
				
		for(User user: users)
			log.debug("Found user: " + user);
		
		this.deleteHelperUser();
	}
	
	@Test
	public void testUpdateAccountPreference() {
		setup();
		this.insertHelperUser();
		
		AccountPreference updatedPref = new AccountPreference("12345", "Bob", "Hope", new Date(), new Address(), "1234", null);
		
		boolean success = userRepo.updateAccountPreferences(this.helperUser, updatedPref);
		
		assertTrue(success);
		log.debug(this.helperUser.toString());
		
		this.deleteHelperUser();
	}
	
	@Test
	public void testUpdateAddress() {
		setup();
		this.insertHelperUser();
		
		Address address = new Address("Orion", "1234", "91406", "United States", "Van Nuys", "Los Angeles", "Some code");
		new Address();
		
		boolean success = userRepo.updateAddress(this.helperUser, address);
		
		assertTrue(success);
		log.debug(this.helperUser.toString());
		
		this.deleteHelperUser();
	}
	
	@Test
	public void testSettingCurrentLocation() {
		setup();
		this.insertHelperUser();
		
		String randomLocationStr = "ab321s";
		
		boolean success = userRepo.updateCurrentGeoLocation(this.helperUser, randomLocationStr);
		
		assertTrue(success);
		log.debug(this.helperUser.toString());
		
		this.deleteHelperUser();
	}
	
	
	@Test
	public void testSettingPreferredLocations() {
		setup();
		this.insertHelperUser();
		
		Address address = new Address("Orion", "1234", "91406", "United States", "Van Nuys", "Los Angeles", "Some code");
		List<Address> preferredLocations = new ArrayList<Address>();
		preferredLocations.add(address);
		preferredLocations.add(address);
		
		boolean success = userRepo.updatePreferredLocations(this.helperUser, preferredLocations);
		
		assertTrue(success);
		log.debug(this.helperUser.toString());
		
		this.deleteHelperUser();
	}
}
