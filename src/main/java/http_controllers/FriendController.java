package http_controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import database_entities.User;
import service.AccountCredentialService;
import service.FriendsService;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;

@Controller
@RequestMapping(value="/user/friends/**")
public class FriendController {

	public static final Logger log = LoggerFactory.getLogger("controller-log");
	
	@Autowired private FriendsService friendService;
	@Autowired private AccountCredentialService accountService;
	
	@RequestMapping(value="/sendFriendRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage sendFriendRequest(
			@RequestParam(value="username", required=true) String username,
			HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		return friendService.sendFriendRequest(user, username);
	}
	
	//Handles ACCEPT, DENY, BLOCK of a friend request
	@RequestMapping(value="/respondToFriendRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage respondToFriendRequest(
			@RequestParam(value="username", required=true) String username,
			@RequestParam(value="response", required=true) String response,		//ACCEPT, DENY, BLOCK
			HttpServletRequest request
			) {
				
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		String check = response.toUpperCase();
		
		if(check.compareTo("ACCEPT") == 0)
			return friendService.acceptFriendRequest(user, username);
		else if(check.compareTo("DENY") == 0)
			return friendService.denyFriendRequest(user, username);
		else if(check.compareTo("BLOCK") == 0)
			return friendService.blockFriendRequest(user, username);
		else
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_INVALID);	
	}
	
	
	/*
	 * 
	 * GET METHODS
	 * 
	 */
	
	@RequestMapping(value="/retrieveAllFriends", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage retrieveAllFriends(HttpServletRequest request) {
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		return friendService.getAllFriends(user);
	}
	
	
	@RequestMapping(value="/retrievePendingFriendRequests", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage retrievePendingFriendRequests(HttpServletRequest request) {
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		return friendService.retrievePendingFriendRequests(user);
	}
	
	@RequestMapping(value="/retrieveBlockedFriends", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage retrieveBlockedFriends(HttpServletRequest request) {
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		return friendService.retrieveBlockedList(user);
	}
}
