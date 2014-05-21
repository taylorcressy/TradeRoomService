/**
 * Class associated with all server logic surrounding credential handling of a User.
 * This includes activities like logging in, registering, loggin out, updating account details,
 * requesting a password reset, etc.
 * 
 * Moreover, this class will be the connector for Session Handling
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @date 15 April, 2014
 */

package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import server_utilities.DefaultProperties;
import server_utilities.JavaHasher;
import database_entities.AccountPreference;
import database_entities.Address;
import database_entities.TradeItem;
import database_entities.TradeRoomMeta;
import database_entities.User;
import database_entities.UserRepository;
import database_entities.exceptions.DetailedDuplicateKeyException;
import database_entities.exceptions.StatusMessageDoesNotExist;

@Service
public class AccountCredentialService {

	private static final int MIN_NAME_LENGTH = 2;
	private static final int MIN_PASS_LENGTH = 4;
	private static final int MAX_NAME_PASS_LENGTH = 32;
	private static final int MAX_STREET_NAME_LENGTH = 35;
	private static final int MIN_STREET_NAME_LENGTH = 1;
	private static final int MAX_STREET_NUM_LENGTH = 8;
	private static final int MIN_STREET_NUM_LENGTH = 1;
	private static final int MAX_AREA_CODE_LENGTH = 8;
	private static final int MIN_AREA_CODE_LENGTH = 2;
	private static final int MAX_CITY_LENGTH = 28;
	private static final int MIN_CITY_LENGTH = 2;
	
	
	private static final Logger log = LoggerFactory.getLogger("service-logger");
	
	@Autowired
	private UserRepository repo;
	@Autowired
	private StatusMessagesAndCodesService messageService;
	@Autowired
	private ValidCountryService countryService;
	@Autowired
	private DefaultProperties defaultProperties;

	private Gson gson;
	
	public AccountCredentialService() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	
	/**
	 * Register a user.
	 * 
	 * 
	 * @param username
	 * @param email
	 * @param password
	 * @param firstName
	 * @param lastName
	 * 
	 * @return ServerMessage
	 */
	public ServerMessage registerNewUser(String username, String email, String password, String firstName, String lastName)
			throws StatusMessageDoesNotExist {

		HashMap<String, String> invalidFormMap = new HashMap<String, String>();
		
		if (username.trim().length() < MIN_NAME_LENGTH || username.trim().length() > MAX_NAME_PASS_LENGTH) {
			invalidFormMap.put("invalidEntry", "username");
			invalidFormMap.put("reason", "length");
			return messageService.getMessageWithData(StatusMessagesAndCodesService.REGISTRATION_INVALID_FORM, this.gson.toJson(invalidFormMap));
		}

		if (isEmailAddressSyntaxValid(email.trim()) == false) {
			invalidFormMap.put("invalidEntry", "email");
			invalidFormMap.put("reason", "syntax");
			return messageService.getMessageWithData(StatusMessagesAndCodesService.REGISTRATION_INVALID_FORM, this.gson.toJson(invalidFormMap));
		}

		if (isPasswordSyntaxValid(password.trim()) == false) {
			invalidFormMap.put("invalidEntry", "password");
			invalidFormMap.put("reason", "syntax");
			return messageService.getMessageWithData(StatusMessagesAndCodesService.REGISTRATION_INVALID_FORM, this.gson.toJson(invalidFormMap));
		}

		if (firstName.trim().length() < MIN_NAME_LENGTH || firstName.trim().length() > MAX_NAME_PASS_LENGTH) {
			invalidFormMap.put("invalidEntry", "firstName");
			invalidFormMap.put("reason", "length");
			return messageService.getMessageWithData(StatusMessagesAndCodesService.REGISTRATION_INVALID_FORM, this.gson.toJson(invalidFormMap));
		}

		if (lastName.trim().length() < MIN_NAME_LENGTH || lastName.trim().length() > MAX_NAME_PASS_LENGTH) {
			invalidFormMap.put("invalidEntry", "lastName");
			invalidFormMap.put("reason", "length");
			return messageService.getMessageWithData(StatusMessagesAndCodesService.REGISTRATION_INVALID_FORM, this.gson.toJson(invalidFormMap));
		}

		AccountPreference pref = new AccountPreference();
		pref.setPasswordHash(JavaHasher.sha512(password.trim()));
		pref.setFirstName(firstName.trim());
		pref.setLastName(lastName.trim());
		
		TradeRoomMeta metaRoom = new TradeRoomMeta(defaultProperties.getIntProperty("startRoomSize"), 
													defaultProperties.getIntProperty("maxNumberOfImages"), 0, null, null);

		User user = new User(username.trim().toLowerCase() , email.trim().toLowerCase(), pref, metaRoom, null, null, null, new Date());

		try {
			repo.save(user);
			return messageService.getMessageWithData(StatusMessagesAndCodesService.REGISTRATION_OKAY, this.gson.toJson(user));
		} catch (DetailedDuplicateKeyException e) {
			if (e.getDuplicatedIndex().compareTo("email") == 0) {
				return messageService.getMessageForCode(StatusMessagesAndCodesService.REGISTRATION_DUPLICATE_EMAIL);
			} else if (e.getDuplicatedIndex().compareTo("username") == 0) {
				return messageService.getMessageForCode(StatusMessagesAndCodesService.REGISTRATION_DUPLICATE_USERNAME);
			} else {
				return messageService.getMessageForCode(StatusMessagesAndCodesService.UNKNOWN_SERVER_ERROR);
			}
		}
	}

	/**
	 * Header method for the two login private methods. This method check to see
	 * if the user is logging in with an email or a username
	 * 
	 * @param Username
	 * @param password
	 */
	public ServerMessage login(String username, String password) {
		if (AccountCredentialService.isEmailAddressSyntaxValid(username))
			return this.verifyEmailPasswordLogin(username, password);
		else
			return this.verifyUsernamePasswordLogin(username, password);
	}
	
	/**
	 * Verify credentials to allow the user to login.
	 * 
	 * Username/password check
	 * 
	 * @param Username
	 * @param Password
	 * @return ServerMessage
	 */
	private ServerMessage verifyUsernamePasswordLogin(String username, String password) {
		if (username.trim().length() < MIN_NAME_LENGTH || username.trim().length() > MAX_NAME_PASS_LENGTH)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_USER_PASS_BAD);
		if (isPasswordSyntaxValid(password.trim()) == false)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_USER_PASS_BAD);

		User user = repo.findOneByUsername(username.toLowerCase());

		if (user == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_USER_PASS_BAD);
		else {
			String hashed = JavaHasher.sha512(password.trim());
			if (hashed.compareTo(user.getAccountPreference().getPasswordHash()) == 0) {
				user.setLastLogin(new Date());
				repo.save(user);
				return messageService.getMessageWithData(StatusMessagesAndCodesService.LOGIN_OKAY, user);
			} else
				return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_USER_PASS_BAD);
		}
	}

	/**
	 * Verify credentials to allow the user to login.
	 * 
	 * Email/password check
	 * 
	 * @param email
	 * @param password
	 * @return ServerMessage
	 */
	private ServerMessage verifyEmailPasswordLogin(String email, String password) {
		// Email check is done in header method
		if (isPasswordSyntaxValid(password.trim()) == false)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_EMAIL_PASS_BAD);

		User user = repo.findOneByEmail(email.toLowerCase());

		if (user == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_EMAIL_PASS_BAD);
		else {
			String hashed = JavaHasher.sha512(password.trim());
			if (hashed.compareTo(user.getAccountPreference().getPasswordHash()) == 0) {
				user.setLastLogin(new Date());
				repo.save(user);
				return messageService.getMessageWithData(StatusMessagesAndCodesService.LOGIN_OKAY, user);
			}
			else
				return messageService.getMessageForCode(StatusMessagesAndCodesService.LOGIN_EMAIL_PASS_BAD);
		}

	}
	
	
	/**
	 * Updat the user's email
	 * 
	 * @param email
	 * @return ServerMessage
	 */
	public ServerMessage updateUserEmailAddress(User user, String email) {
		if(email == null || user == null)
			throw new IllegalArgumentException("Null Values");
		
		if(isEmailAddressSyntaxValid(email) == false)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_EMAIL_FAILED_INVALID_EMAIL);
		
		String previousEmail = user.getEmail();
		
		try {
			user.setEmail(email.toLowerCase());
			this.repo.save(user);
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_EMAIL_SUCCESS, this.gson.toJson(user));
		}
		catch(DetailedDuplicateKeyException ddke) {
			user.setEmail(previousEmail);
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_EMAIL_FAILED_DUPLICATE_EMAIL);
		}
	}
	
	
	
	/**
	 * Update the user's account preferences. 
	 * 
	 * NOTE: This performs no CRUD operations on the User's Address / geoLocations. That should be handled
	 * separately
	 * 
	 * The ServerMessage returned will provide the update user JSON String upon success.
	 * 
	 * @param User
	 * @param Password
	 * @param FirstName
	 * @param LastName
	 * @param DOB - accepted as dd-MM-yyyy
	 */
	public ServerMessage updateUserPreferences(User user, String password, String firstName, String lastName, String dob) {
		
		Date date = null;
		
		if(dob != null) {
			date = AccountCredentialService.convertToDate(dob);
		
			if(date == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_PREF_FAIL_DATE);
		}
		
		//Run check on the credentials
		
		HashMap<String, String> invalidFormMap = new HashMap<String, String>();
		String hash = null;
		if(password != null) {
			if(AccountCredentialService.isPasswordSyntaxValid(password.trim())) {
				hash = JavaHasher.sha512(password.trim());
			}
			else {
				invalidFormMap.put("invalidEntry", "password");
				invalidFormMap.put("reason", "syntax");
				return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_PREF_FAIL_FORM, this.gson.toJson(invalidFormMap));
			}
		}
		
		if(firstName != null) {
			if (firstName.trim().length() < MIN_NAME_LENGTH || firstName.trim().length() > MAX_NAME_PASS_LENGTH) {
				invalidFormMap.put("invalidEntry", "firstName");
				invalidFormMap.put("reason", "length");
				return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_PREF_FAIL_FORM, this.gson.toJson(invalidFormMap));
			}
		}

		if(lastName != null) {
			if (lastName.trim().length() < MIN_NAME_LENGTH || lastName.trim().length() > MAX_NAME_PASS_LENGTH) {
				invalidFormMap.put("invalidEntry", "lastName");
				invalidFormMap.put("reason", "length");
				return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_PREF_FAIL_FORM, this.gson.toJson(invalidFormMap));
			}
		}
		//Create the User Preference Object
		AccountPreference pref = new AccountPreference(hash, firstName.trim(), lastName.trim(), date, null, null, null);
		
		//Request the update from the repository
		boolean success = repo.updateAccountPreferences(user, pref);
		
		if(!success)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		
		return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_PREF_SUCCESS, this.gson.toJson(user));
	}
	
	/**
	 * Update the user's Address details
	 * 
	 * @param StreetName
	 * @param StreetNumber
	 * @param areaCode
	 * @param country
	 * @param city
	 * @param county
	 */
	public ServerMessage updateUserAddress(User user, String streetName, String streetNumber, String areaCode, String country, String city, String county, String geoLocation) {
		
		
		//TODO: Implement the invalidFormMap 
		HashMap<String, String> invalidFormMap = new HashMap<String, String>();
		
		if(streetName.trim().length() < MIN_STREET_NAME_LENGTH || streetName.trim().length() > MAX_STREET_NAME_LENGTH)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ADDR_FAIL_FORM);
			
		if(streetNumber.trim().length() < MIN_STREET_NUM_LENGTH || streetNumber.trim().length() > MAX_STREET_NUM_LENGTH)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ADDR_FAIL_FORM);
		
		if(areaCode.trim().length() < MIN_AREA_CODE_LENGTH || areaCode.trim().length() > MAX_AREA_CODE_LENGTH)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ADDR_FAIL_FORM);
		
		if(countryService.getValidCountry(country.trim()) == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ADDR_FAIL_FORM);
		
		if(city.trim().length() < MIN_CITY_LENGTH || city.trim().length() > MAX_CITY_LENGTH)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ADDR_FAIL_FORM);
		
		if(county.trim().length() < MIN_CITY_LENGTH || county.trim().length() > MAX_CITY_LENGTH)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ADDR_FAIL_FORM);

		Address address = new Address(streetName, streetNumber, areaCode, country, city, county, geoLocation);
		
		if(repo.updateAddress(user, address))
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_ADDR_SUCCESS, this.gson.toJson(user));
		else
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
	}
	
	/**
	 * Set the user's current location
	 * 
	 * @param currentGeoLocation
	 */
	public ServerMessage updateUserCurrentLocation(User user, String geoLocation) {
			
		boolean success = repo.updateCurrentGeoLocation(user, geoLocation);
		
		if(success)
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_CURRENT_LOC_SUCCESS, this.gson.toJson(user));
		else
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_CURRENT_LOC_FAILED);
	}
	
	/**
	 * Update the user's preferred Locations for meeting
	 * 
	 * @param List<Address>
	 */
	
	
	
	/**
	 * Add item reference to the User object
	 * 
	 * @param User
	 * @param TradeItem
	 * @return boolean
	 */
	public boolean addTradeItemToUser(User user, TradeItem item) {
		if(item == null || user == null)
			throw new IllegalArgumentException("Null values");
		
		if(user.getTradeRoomMeta().getItemIds() == null)
			user.getTradeRoomMeta().setItemIds(new ArrayList<String>());
		
		user.getTradeRoomMeta().getItemIds().add(item.getId());
		
		User returnedUser = repo.save(user);
		
		if(returnedUser != null)
			return true;
		else return false;
	}
	
	

	/**
	 * Logout the current user
	 */

	
	public StatusMessagesAndCodesService getMessagingService() {
		return this.messageService;
	}

	/**
	 * This method checks whether the syntax of an email address is valid.
	 * 
	 * @param emailAddress
	 * @return boolean
	 */
	private static boolean isEmailAddressSyntaxValid(String emailAddress) {
		if (emailAddress == null || emailAddress.trim().isEmpty() || emailAddress.trim().length() < 3) {
			return false;
		}

		Pattern pattern;
		Matcher matcher;

		final String EMAIL_ADDRESS_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)";

		pattern = Pattern.compile(EMAIL_ADDRESS_PATTERN);

		matcher = pattern.matcher(emailAddress);

		return matcher.matches();
	}

	/**
	 * This method checks whether the syntax of a password is valid.
	 * 
	 * @param password
	 * @return
	 */
	private static boolean isPasswordSyntaxValid(String password) {

		if (password != null && password.trim().length() > MIN_PASS_LENGTH && password.trim().length() <= MAX_NAME_PASS_LENGTH) {
			return true;
		} else {
			System.out.println(password);
			return false;
		}
	}
	
	/**
	 * Convert the string to an equivalent date object. If the date does not match the string match, return null
	 * 
	 * All dates must be of the format dd-MM-yyyy
	 * 
	 * @param String
	 * @return Date - Object
	 */
	private static Date convertToDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return formatter.parse(date);
		}
		catch(ParseException pe) {
			log.error("Failed to parse Date: " + pe.getLocalizedMessage());
			return null;
		}
	}
}
