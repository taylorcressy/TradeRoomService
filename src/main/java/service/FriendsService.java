/**
 * Provides the business logic for all work associated with friends. 
 * This includes sending and accepting friend requests, retrieving friends/other user's information
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @date 23 April, 2014
 */
package service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import database_entities.FriendRequest;
import database_entities.FriendRequest.FriendRequestStatus;
import database_entities.User;
import database_entities.UserRepository;
import database_entities.exceptions.FatalDatabaseErrorException;
import database_entities.exceptions.FriendRequestDoesNotExistException;
import database_entities.exceptions.FriendRequestExistsException;
import database_entities.exceptions.UserObjectNotInitializedForOperation;

@Service
public class FriendsService {
	
	private static final Logger log = LoggerFactory.getLogger("service-logger");
	
	@Autowired private UserRepository userRepo;
	@Autowired private StatusMessagesAndCodesService messageService;
	
	/**
	 * Retrieve a list of all friends for the user.
	 * 
	 * @param caller
	 * @return ServerMessage
	 */
	public ServerMessage getAllFriends(User caller) {
		if(caller == null)
			throw new IllegalArgumentException("Caller is null");
		
		List<FriendRequest> requests = caller.getFriendRequests();
		
		if(requests == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);

		List<String> names = new ArrayList<String>();
		
		for(FriendRequest next: requests) {
			if(next.getStatus() == FriendRequestStatus.ACCEPTED) {
				if(caller.getUsername().compareToIgnoreCase(next.getReceiverUsername()) == 0) {
					names.add(next.getSenderUsername());
				}
				else
					names.add(next.getReceiverUsername());
			}
		}
		
		if(names.size() == 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);
		
		List<User> users = userRepo.findMultipleUsersByUsername(names);
		
		if(users != null) {
			for(int i=0; i < users.size(); i++) {
				users.set(i, screenUserForFriend(users.get(i)));
			}
		}
		
		log.debug("Retrieved Friends for " + caller.getUsername());
		return messageService.getMessageWithData(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_SUCCESS, new Gson().toJson(users));
	}
	
	/**
	 * Retrieve the User's blocked list friend's data
	 * 
	 * @param User caller
	 * @return ServerMessage
	 */
	public ServerMessage retrieveBlockedList(User caller) {
		if(caller == null)
			throw new IllegalArgumentException("Caller is null");
		
		List<FriendRequest> requests = caller.getFriendRequests();
		
		if(requests == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);

		List<String> names = new ArrayList<String>();
		
		for(FriendRequest next: requests) {
			if(next.getStatus() == FriendRequestStatus.BLOCKED) {
				if(caller.getUsername().compareToIgnoreCase(next.getReceiverUsername()) == 0) {
					names.add(next.getSenderUsername());
				}
				else
					names.add(next.getReceiverUsername());
			}
		}
		
		if(names.size() == 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);
		
		List<User> users = userRepo.findMultipleUsersByUsername(names);
		
		if(users != null) {
			for(int i=0; i < users.size(); i++) {
				users.set(i, screenUserForNonFriend(users.get(i)));
			}
		}
		
		log.debug("Retrieved Blocked List for " + caller.getUsername());
		return messageService.getMessageWithData(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_SUCCESS, new Gson().toJson(users));
	}
	
	
	/**
	 * Retrieve list of pending friend requests
	 * 
	 * @param User
	 * @return ServerMessage
	 */
	public ServerMessage retrievePendingFriendRequests(User caller) {
		if(caller == null)
			throw new IllegalArgumentException("Caller is null");
		
		List<FriendRequest> requests = caller.getFriendRequests();
		
		if(requests == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);

		List<String> names = new ArrayList<String>();
		
		for(FriendRequest next: requests) {
			if(next.getStatus() == FriendRequestStatus.PENDING) {
				if(caller.getUsername().compareToIgnoreCase(next.getReceiverUsername()) == 0) {
					names.add(next.getSenderUsername());
				}
				else
					names.add(next.getReceiverUsername());
			}
		}
		
		if(names.size() == 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);
		
		List<User> users = userRepo.findMultipleUsersByUsername(names);
		
		if(users != null) {
			for(int i=0; i < users.size(); i++) {
				users.set(i, screenUserForNonFriend(users.get(i)));
			}
		}
		
		log.debug("Retrieved Friends for " + caller.getUsername());
		return messageService.getMessageWithData(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_SUCCESS, new Gson().toJson(users));
	}
	
	
	
	/**
	 * Logic for sending a friend request
	 * 
	 * @param caller
	 * @param username
	 * @return ServerMessage
	 */
	public ServerMessage sendFriendRequest(User caller, String username) {
		if(caller == null || username == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.sendFriendRequest(caller, username);
			
			if(success) 
				return messageService.getMessageWithData(StatusMessagesAndCodesService.SEND_FRIEND_REQ_SUCCESS, new Gson().toJson(caller));
			else 
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_FRIEND_REQ_FAIL_NO_USER);
		}
		catch(FriendRequestExistsException free) {
			switch(free.getStatus()) {
			case ACCEPTED:
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_FRIEND_REQ_FAIL_FRIEND_EXISTS);
			case BLOCKED:
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_FRIEND_REQ_FAIL_BLOCKED);
			case DENIED:
				//SHOULD NEVER GET HERE. ALL DENIED REQUESTS ARE IMMEDIATELY DESTROYED
				return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
			case PENDING:
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_FRIEND_REQ_FAIL_PENDING);
			default:
				return null;
			}
		}
		catch(FatalDatabaseErrorException fdee) {
			return messageService.getMessageWithData(StatusMessagesAndCodesService.DATABASE_ERROR, fdee.getLocalizedMessage());
		}
	}
	
	/**
	 * Accept the user's friend request
	 * 
	 * @param Caller
	 * @param String username
	 * @return ServerMessage
	 */
	public ServerMessage acceptFriendRequest(User caller, String friendUsername) {
		if(caller == null || friendUsername == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.acceptFriendRequest(caller, friendUsername);
			
			if(success)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_SUCCESS);
			else	//All other cases should be handled by catch blocks, so this is an unknown error
				return messageService.getMessageForCode(StatusMessagesAndCodesService.UNKNOWN_SERVER_ERROR);
		}
		catch(FriendRequestExistsException free) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_DUPLICATE);
		}
		catch(FriendRequestDoesNotExistException frdnee) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_NONE);
		}
		catch(FatalDatabaseErrorException fdee) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		}
		catch(UserObjectNotInitializedForOperation exc) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_NONE);
		}
	}
	
	/**
	 * Deny the user's friend request
	 * 
	 * @param Caller
	 * @param String username
	 * @return ServerMessage
	 */
	public ServerMessage denyFriendRequest(User caller, String friendUsername) {
		if(caller == null || friendUsername == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.denyFriendRequest(caller, friendUsername);
			
			if(success)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_SUCCESS);
			else	//All other cases should be handled by catch blocks, so this is an unknown error
				return messageService.getMessageForCode(StatusMessagesAndCodesService.UNKNOWN_SERVER_ERROR);
		}
		catch(FriendRequestDoesNotExistException frdnee) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_NONE);
		}
		catch(FatalDatabaseErrorException fdee) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		}
		catch(UserObjectNotInitializedForOperation exc) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_NONE);
		}
	}
	
	/**
	 * Deny the user's friend request
	 * 
	 * @param Caller
	 * @param String username
	 * @return ServerMessage
	 */
	public ServerMessage blockFriendRequest(User caller, String friendUsername) {
		if(caller == null || friendUsername == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.blockFriendRequest(caller, friendUsername);
			
			if(success)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_SUCCESS);
			else	//All other cases should be handled by catch blocks, so this is an unknown error
				return messageService.getMessageForCode(StatusMessagesAndCodesService.UNKNOWN_SERVER_ERROR);
		}
		catch(FriendRequestExistsException free) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_DUPLICATE);
		}
		catch(FriendRequestDoesNotExistException frdnee) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_NONE);
		}
		catch(FatalDatabaseErrorException fdee) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		}
		catch(UserObjectNotInitializedForOperation exc) {
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_NONE);
		}
	}
	
	/**
	 * Screen data that other user's should not see so that the User object can be safely transfered over the wire
	 * 
	 * This would be where Security Preferences would be incorporated! A hard-coded recommendation is used for now
	 * 
	 * @param user
	 * @return ScreenedUser
	 */
	private User screenUserForFriend(User user) {
		if(user.getAccountPreference() != null) 
			user.getAccountPreference().setAddress(null);
		
		user.setTradeRequests(null);
		user.setFriendRequests(null);
		return user;
	}
	
	/**
	 * Screen data that non-friends should see so that the User object can be safely transfered over the wire
	 * 
	 * This would be where Security Preferences would be incorporated. A hard-coded recommendation is used for now
	 * 
	 * @param user
	 * @return ScreenedUser
	 */
	private User screenUserForNonFriend(User user) {
		user.setAccountPreference(null);
		user.setEmail(null);
		user.setFriendRequests(null);
		user.setTradeRequests(null);
		return user;
	}
}

