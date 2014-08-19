package http_controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import database_entities.User;
import database_entities.repositories.UserRepository;
import service.AccountCredentialService;
import service.GeoLocationService;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;

@Controller
@RequestMapping(value="/location/**")
public class GeoLocationController {

	@Autowired
	private AccountCredentialService accountService;
	
	@Autowired
	private GeoLocationService service;
	
	@Autowired
	private UserRepository repo;
	
	
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
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		}
		
		return service.updateGeoLocation(user, city, longitude, latitude);		
	}
}
