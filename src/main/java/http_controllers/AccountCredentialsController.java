/**
 * Handles all http requests regarding Account Credentials. This includes Session related queries
 */
package http_controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import service.AccountCredentialService;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;
import database_entities.User;

@Controller
@RequestMapping(value="/user/**")
public class AccountCredentialsController {	
		
	@Autowired private AccountCredentialService accountService;	
	
	private Gson gson;
	
	public AccountCredentialsController() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	
	@RequestMapping(value="/getAccountDetails", method=RequestMethod.GET) 
	public @ResponseBody
	ServerMessage getAccountDetailsOfLoggedInUser(HttpServletRequest request) {
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		}
		else {
			return accountService.getMessagingService().getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, this.gson.toJson(user));
		}
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public @ResponseBody 
	ServerMessage createAccountForUser(
			@RequestParam(required=true, value="username") String username,
			@RequestParam(required=true, value="password") String password,
			@RequestParam(required=true, value="email") String email,
			@RequestParam(required=true, value="firstName") String firstName,
			@RequestParam(required=true, value="lastName") String lastName
			) {
		return accountService.registerNewUser(username, email, password, firstName, lastName);
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage loginUser(
			@RequestParam(required=true, value="username") String username,
			@RequestParam(required=true, value="password") String password,
			HttpServletRequest request
			) {
		
		ServerMessage message = accountService.login(username, password);
		
		if(message.getCode() == StatusMessagesAndCodesService.LOGIN_OKAY) {
			if(SessionHandler.setUserToSession(request, (User) message.getData())) {
				//Use Gson to send the user object over the wire as to respect transient variables.
				message.setData(this.gson.toJson(message.getData()));
				return message;
			}
			else {
				return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_SET_FAILED);
			}
		}
		else 
			return message;
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage logoutUser(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		
		if(session == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		session.invalidate();
		
		return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_OKAY);
	}
	
	@RequestMapping(value="/updateEmail", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage updateEmail(
			@RequestParam(value="email", required = true) String email,
			HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		}
		
		return accountService.updateUserEmailAddress(user, email);
	}
	
	/**
	 * Update the user's Account Preferences
	 * 
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param dob
	 * @param request
	 * @return ServerMessage
	 */
	@RequestMapping(value="/updatePreferences", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage updatePreferences(
			@RequestParam(required=false, value="password") String password,
			@RequestParam(required=false, value="firstName") String firstName,
			@RequestParam(required=false, value="lastName") String lastName,
			@RequestParam(required=false, value="dob") String dob,
			HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		return accountService.updateUserPreferences(user, password, firstName, lastName, dob);
	}
	
	/**
	 * Update the user's Address
	 * 
	 * @param streetName
	 * @param streetNumber
	 * @param areaCode
	 * @param country
	 * @param county
	 * @param city
	 * @param geoLocation
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateAddress", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage updateAddress(
			@RequestParam(required=true, value="streetName") String streetName,
			@RequestParam(required=true, value="streetNumber") String streetNumber,
			@RequestParam(required=true, value="areaCode") String areaCode,
			@RequestParam(required=true, value="country") String country,
			@RequestParam(required=true, value="county") String county,
			@RequestParam(required=true, value="city") String city,
			@RequestParam(required=true, value="geoLocation") String geoLocation,
			HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		return accountService.updateUserAddress(user, streetName, streetNumber, areaCode, country, city, county, geoLocation);
	}
	
	/**
	 * Update the user's current location. There will ever only be an update function for the current location.
	 * 
	 * @param geoLocation
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateCurrentLocation", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage updateCurrentLocation(
		@RequestParam(required=true, value="city") String city,
		@RequestParam(required=true, value="longitude") double longitude,
		@RequestParam(required=true, value="latitude") double latitude,
		HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		return accountService.updateUserCurrentLocation(user, city, longitude, latitude);
	}
}
