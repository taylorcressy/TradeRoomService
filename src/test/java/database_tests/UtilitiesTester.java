package database_tests;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import service.ServerMessage;
import service.StatusMessagesAndCodesService;
import service.ValidCountryService;
import service.ValidCountryService.ValidCountry;
import config.AppConfig;
import database_entities.exceptions.LoadDBWithCSVFailed;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class UtilitiesTester {
	
	private static Logger log = LoggerFactory.getLogger("database-test-logger");
	
	@Autowired StatusMessagesAndCodesService codesService;
	@Autowired ValidCountryService countryService;
	
	@Test
	public void testMessage() {
		try {
			String expectedMessage = "Registration Failed: Invalid Form";
			codesService.populateDBWithCSVErrors();
			ServerMessage message = codesService.getMessageForCode(102);
			log.debug("Returned message: " + message.getMessage());
			assertTrue(message.getMessage().compareTo(expectedMessage)==0);
		}
		catch(LoadDBWithCSVFailed csv) {
			log.error("Failed to load csv file into the DB: " + csv.getLocalizedMessage());
			fail(csv.getLocalizedMessage());
		}
	}
	
	@Test
	public void testValidCountries() {
	
		Collection<ValidCountry> countryList = countryService.retrieveListOfValidCountries();
		
		assertNotNull(countryList);
		
		ValidCountry country =  countryService.getValidCountry("United States");
		assertNotNull(country);
		log.debug("United States Object: " + country);
	}
}
