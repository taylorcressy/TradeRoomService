/**
 * Test cases for the User Collections in MongoDB
 * 
 * @author Taylor Cressy
 */
package database_tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import database_entities.AccountPreference;
import database_entities.Address;
import database_entities.RepositoryFactory;
import database_entities.User;

public class TestUserDatabaseHandling {

	private final Logger log = LoggerFactory.getLogger("database-test-logger"); // LoggerFactory.getLogger(TestUserCollection.class);
	
	private User primaryUser;

	private void setup() {		
		AccountPreference pref = new AccountPreference("1234", "Taylor", "Cressy", "11/01/1991", new Address("Mary Lane", "1234",
				"KT1 1Ty", "England", "Norbiton", "London", "12345 - 12345"), "12345 - 12345", null);

		ArrayList<Integer> ranks = new ArrayList<Integer>();
		ranks.add(5);
		ranks.add(3);
		ranks.add(4);
		ranks.add(3);

		this.primaryUser = new User("rayred", pref, ranks, null, null);
	}

	private void takeDown() {
		RepositoryFactory.shutdownMongoOperations();
	}

	@Test
	public void CRUDUser() {
		setup();		
		boolean success = this.primaryUser.createNewUser();
		assertTrue(success);
		log.debug("Created user " + this.primaryUser);

				
		User readUser = new User("rayred");
		success = readUser.readUser();
		assertTrue(success);
		log.debug("Read user " + readUser);
		
		//Update the User by clearing all of its meta data, and read to see if it's null to check for success
		User toUpdate = new User("rayred");
		success = toUpdate.updateUser();
		assertTrue(success);
		log.debug("Updated user to " + toUpdate);
		
		success = toUpdate.readUser();
		assertTrue(success);
		assertTrue(toUpdate.getAccountPreference() == null);
		log.debug("Verified updated user as " + toUpdate);
		
		//Delete the user to keep db clear (Clear the entry manually in DB if delete is faulty)
		success = this.primaryUser.deleteUser();
		assertTrue(success);
		log.debug("Successfully deleted the user");
		
		takeDown();
	}

	
}
