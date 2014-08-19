package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import database_entities.User;
import database_entities.repositories.UserRepository;

@Service
public class GeoLocationService {

	
	private static final Logger log = LoggerFactory.getLogger("service-logger");
	
	@Autowired
	private UserRepository repo;
	@Autowired
	private StatusMessagesAndCodesService messageService;
	
	
	/**
	 * Send a request to the DB to update the user's current location
	 * */
	public ServerMessage updateGeoLocation(User user, String city, double longitude, double latitude) {
		double [] position = {longitude, latitude};
		user.setCity(city);
		user.setPosition(position);
		user = repo.save(user);
		if(user == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_GEOLOCATION_FAILED);
		else
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_GEOLOCATION_SUCCESS);
	}
}
