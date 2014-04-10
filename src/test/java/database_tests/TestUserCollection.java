/**
 * Test cases for the User Collections in MongoDB
 * 
 * @author Taylor Cressy
 */
package database_tests;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.WriteConcern;

import database_entities.AccountPreference;
import database_entities.Address;
import database_entities.User;

public class TestUserCollection {

	private final Logger log = LoggerFactory.getLogger("database-test-logger"); // LoggerFactory.getLogger(TestUserCollection.class);
	private MongoTemplate operations;
	private User primaryUser;
	GenericXmlApplicationContext context;

	private void setup() {
		this.context = new GenericXmlApplicationContext("SpringConfig.xml");
		this.operations = (MongoTemplate) context.getBean("mongoTemplate");

		// Handle entities
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1991);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date dob = cal.getTime();
		AccountPreference pref = new AccountPreference("1234", "Taylor", "Cressy", new Timestamp(dob.getTime()), new Address("Mary Lane", "1234",
				"KT1 1Ty", "England", "Norbiton", "London", "12345 - 12345"), "12345 - 12345", null); 
		
		ArrayList<Integer> ranks = new ArrayList<Integer>();
		ranks.add(5);
		ranks.add(3);
		ranks.add(4);
		ranks.add(3);
		
		this.primaryUser = new User("rayred", pref, ranks, null, null);

		this.operations.setWriteConcern(WriteConcern.ACKNOWLEDGED);
	}

	private void takeDown() {
		this.context.close();
	}

	@Test
	public void insertAUser() {
		setup();
		// Save
		try {
			this.operations.insert(this.primaryUser);
		} catch (DuplicateKeyException dupKey) {
			log.debug("Duplicate Key: " + dupKey.getLocalizedMessage());
		}

		this.takeDown();
	}
}
