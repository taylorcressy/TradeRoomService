package http_controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spockframework.gentyref.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	
	private Gson gson;
	
	public FriendController() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	
	@RequestMapping(value="/sendFriendRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage sendFriendRequest(
			@RequestParam(value="userId", required=true) String userId,
			HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		return friendService.sendFriendRequest(user, userId);
	}
	
	//Handles ACCEPT, DENY, BLOCK of a friend request
	@RequestMapping(value="/respondToFriendRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage respondToFriendRequest(
			@RequestParam(value="userId", required=true) String userId,
			@RequestParam(value="status", required=true) String response,		//ACCEPT, DENY, BLOCK
			HttpServletRequest request
			) {
				
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		String check = response.toUpperCase();
		
		if(check.compareTo("ACCEPTED") == 0)
			return friendService.acceptFriendRequest(user, userId);
		else if(check.compareTo("DENIED") == 0)
			return friendService.denyFriendRequest(user, userId);
		else if(check.compareTo("BLOCK") == 0)
			return friendService.blockFriendRequest(user, userId);
		else
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.RESPONSE_FRIEND_REQ_FAIL_INVALID);	
	}
	
	
	/*
	 * 
	 * GET METHODS
	 * 
	 */
	
	@RequestMapping(value="/getAccountDetailsOfUser", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage getAccoundDetailsOfUser(
			@RequestParam(value="userId", required=true) String userId,
			HttpServletRequest request
			) 
	{
		User user = SessionHandler.getUserForSession(request);
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		else 
			return friendService.getAccountDetailsOfUserWithId(user, userId);
	}
	
	@RequestMapping(value="/retrieveUsersWithIds", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage retrieveAllUsersWithIds(
			@RequestParam(value="userIds", required=true) String userIdsJson,
			HttpServletRequest request
			) {
				
		User user = SessionHandler.getUserForSession(request);
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		else {
			List<String> userIds = this.gson.fromJson(userIdsJson, new TypeToken<List<String>>(){}.getType());
			if(userIds == null)
				return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
			else
				return friendService.getAllUsersWithIds(user, userIds);
		}
		
	}
	
	@RequestMapping(value="/retrieveAllUsersWithFacebookIds", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage retrieveAllUsersWithFacebookIds(
			@RequestParam(value="facebookIds", required=true) String facebookIdJson,
			HttpServletRequest request
			) {
		User user = SessionHandler.getUserForSession(request);
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		else {
			List<String> facebookIds = this.gson.fromJson(facebookIdJson, new TypeToken<List<String>>(){}.getType());
			if(facebookIds == null)
				return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
			else
				return friendService.getAllFacebookUsers(user, facebookIds);
		}
	}
	
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
