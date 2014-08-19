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
import database_entities.exceptions.FatalDatabaseErrorException;
import database_entities.exceptions.FriendRequestDoesNotExistException;
import database_entities.exceptions.FriendRequestExistsException;
import database_entities.exceptions.UserObjectNotInitializedForOperation;
import database_entities.repositories.UserRepository;

@Service
public class FriendsService {
	
	private static final Logger log = LoggerFactory.getLogger("service-logger");
	
	@Autowired private UserRepository userRepo;
	@Autowired private StatusMessagesAndCodesService messageService;
	
	/**
	 * Retrieve the account details associated with specified userId. 
	 * This should be called anytime ANY user wants the account details of ANY other user.
	 * 
	 * @param userId
	 */
	public ServerMessage getAccountDetailsOfUserWithId(User caller, String userId) {
		User user = userRepo.findOne(userId);
		if(user == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_USER_DATA_FAILED_NO_USER);
		
		if(user.getFriendRequests() != null) {
			for(FriendRequest request: user.getFriendRequests()) {
				if(request.getStatus() == FriendRequestStatus.BLOCKED)
					return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_USER_DATA_FAILED_BLOCKED);
			}
		}
		
		if(caller.getFriendRequests() != null) {
			for(FriendRequest request: caller.getFriendRequests()) {
				if(request.getStatus() == FriendRequestStatus.BLOCKED)
					return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_USER_DATA_FAILED_BLOCKED);
			}
		}
		
		return messageService.getMessageWithData(StatusMessagesAndCodesService.RETRIEVE_USER_DATA_SUCCESS, screenUser(user));
	}
	
	/**
	 * Get all user's associated with the user Ids list
	 */
	public ServerMessage getAllUsersWithIds(User caller, List<String> userIds) {
		List<User> allUsers = userRepo.findAllByIdIn(userIds);
		List<User> filteredUsers = new ArrayList<User>();
		//Screen Users if they have blocked each other
		for(User user: allUsers) {
			if(user.getFriendRequests() != null) {
				for(FriendRequest request: user.getFriendRequests()) {
					if(request.getStatus() == FriendRequestStatus.BLOCKED)
						continue;
				}
			}
			
			if(caller.getFriendRequests() != null) {
				for(FriendRequest request: caller.getFriendRequests()) {
					if(request.getStatus() == FriendRequestStatus.BLOCKED)
						continue;
				}
			}
			filteredUsers.add(screenUser(user));
		}
		return messageService.getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, filteredUsers);
	}
	
	/**
	 * Retrieve a list of all user's with the associated Facebook ID
	 * This will filter out all user's that are already friends
	 */
	 public ServerMessage getAllFacebookUsers(User caller, List<String> facebookIds) {
		 List<User> users = userRepo.findAllByFacebookIdIn(facebookIds);
		 List<User> filteredUsers = new ArrayList<User>();
		 List<String> friendIds = new ArrayList<String>();
		 for(FriendRequest request: caller.getFriendRequests()) {
			 if(request.getReceiverId().compareTo(caller.getId()) == 0)
				 friendIds.add(request.getSenderId());
			 else
				 friendIds.add(request.getReceiverId());
		 }
		 for(User next: users) {
			 if(!caller.getFriendRequests().contains(next.getId()))
				 filteredUsers.add(next);
		 }
		 return messageService.getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, filteredUsers);
	 }
	
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

		List<String> ids = new ArrayList<String>();
		
		for(FriendRequest next: requests) {
			if(next.getStatus() == FriendRequestStatus.ACCEPTED) {
				if(caller.getId().compareToIgnoreCase(next.getReceiverId()) == 0) {
					ids.add(next.getSenderId());
				}
				else
					ids.add(next.getReceiverId());
			}
		}
		
		if(ids.size() == 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);
		
		Iterable<User> users = userRepo.findAll(ids);
		
		for(User user: users) {
			user = screenUser(user);
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

		List<String> ids = new ArrayList<String>();
		
		for(FriendRequest next: requests) {
			if(next.getStatus() == FriendRequestStatus.BLOCKED) {
				if(caller.getUsername().compareToIgnoreCase(next.getReceiverId()) == 0) {
					ids.add(next.getSenderId());
				}
				else
					ids.add(next.getReceiverId());
			}
		}
		
		if(ids.size() == 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);
		
		Iterable<User> users = userRepo.findAll(ids);
		
		for(User user: users) {
			user = screenUser(user);
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

		List<String> ids = new ArrayList<String>();
		
		for(FriendRequest next: requests) {
			if(next.getStatus() == FriendRequestStatus.PENDING) {
				if(caller.getUsername().compareToIgnoreCase(next.getReceiverId()) == 0) {
					ids.add(next.getSenderId());
				}
				else
					ids.add(next.getReceiverId());
			}
		}
		
		if(ids.size() == 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RETRIEVE_FRIENDS_FAIL_NONE);
		
		Iterable<User> users = userRepo.findAll(ids);
		
		for(User user: users) {
			user = screenUser(user);
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
	public ServerMessage sendFriendRequest(User caller, String id) {
		if(caller == null || id == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.sendFriendRequest(caller, id);
			
			if(success) 
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_FRIEND_REQ_SUCCESS);
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
	public ServerMessage acceptFriendRequest(User caller, String friendId) {
		if(caller == null || friendId == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.acceptFriendRequest(caller, friendId);
			
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
	public ServerMessage denyFriendRequest(User caller, String friendId) {
		if(caller == null || friendId == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.denyFriendRequest(caller, friendId);
			
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
	public ServerMessage blockFriendRequest(User caller, String friendId) {
		if(caller == null || friendId == null)
			throw new IllegalArgumentException("Caller and/or username in sendFriendRequest is null");
		
		try {
			boolean success = userRepo.blockFriendRequest(caller, friendId);
			
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
	 * 
	 * Remove data that would be sensitive. Meaning, block data from being retrieved from other users
	 * 
	 * @param user
	 * @return ScreenedUser
	 */
	public static User screenUser(User user) {
		user.setAccountPreference(null);
		user.setEmail(null);
		user.setFriendRequests(null);
		user.setTradeRequests(null);
		user.setPosition(null);
		
		return user;
	}
}

