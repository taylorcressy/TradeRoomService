/**
 * Service taking care of all aspects associated with a trade room. This means that it will handle 
 * screening items to other user's and facilitating Trade Requests
 */
package service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database_entities.TradeItemRepository;
import database_entities.TradeRequest;
import database_entities.TradeRequest.TradeMethod;
import database_entities.User;
import database_entities.UserRepository;
import database_entities.TradeRequest.TradeRequestStatus;


@Service
public class TradeRoomService {

	
	public static final Logger log = LoggerFactory.getLogger("service-log");
	
	@Autowired
	private StatusMessagesAndCodesService messageService;
	
	@Autowired private UserRepository userRepo;
	
	@Autowired private TradeItemRepository itemRepo;
	
	private Gson gson;
	
	public TradeRoomService() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	
	/**
	 * 	Retrieve the Trade Room meta details of a user
	 * @param caller
	 * @param targetUser
	 * @return ServerMessage
	 */
	public ServerMessage getTradeRoomOfUser(User caller, String targetUser) {
		/*
		 * 
		 * TODO: Implement security preferences screening user's from viewing other user's trade room
		 * something like. isUserCapableOfViewing(srcUserObj, targetUserObj); 
		 */
		
		//Grab target user
		User targUsrObj = userRepo.findOneByUsername(targetUser);
		
		if(targUsrObj == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_META_RETRV_FAILED_NO_USER);
		
		//Return the trade room meta
		return messageService.getMessageWithData(StatusMessagesAndCodesService.TRADE_META_RETRV_SUCCESS, this.gson.toJson(targUsrObj.getTradeRoomMeta()));
	}
	
	/*
	 * 	Trade Request Handling
	 */
	
	/**
	 * Send a trade request to a user with the requested trade items and the requested trade methods
	 * 
	 * @param caller
	 * @param toUser
	 * @param callerIds
	 * @param targetIds
	 * @param TradeMethod
	 * @return ServerMessage
	 */
	public ServerMessage sendTradeRequestToUser(User caller, String toUser, List<String> callerIds, List<String> targetIds, String tradeMethod) {
		
		User targUsrObj = userRepo.findOneByUsername(toUser);
		
		if(targUsrObj == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_NO_USER);
		
		/*
		 * 	Ensure that all the items given are contained by the appropriate User Object
		 */
		List<String> callerCurrentItems = caller.getTradeRoomMeta().getItemIds();
		List<String> targetCurrentItems = targUsrObj.getTradeRoomMeta().getItemIds();
		
		if(targetCurrentItems == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_NO_ITEMS);
		if(callerCurrentItems == null && callerIds.size() > 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		if(targetCurrentItems.size() < targetIds.size())
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		if(callerCurrentItems.size() < callerIds.size())
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		
		for(String nextId: callerIds) {
			if(callerCurrentItems.contains(nextId) == false)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS); 
		}
		for(String nextId: targetIds) {
			if(targetCurrentItems.contains(nextId) == false)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		}
		
		//All IDs are valid from this point forward, so create the trade request and save it to both users
		TradeMethod method = TradeMethod.valueOf(tradeMethod);
		
		if(method == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_TRADE_METHOD);
		
		TradeRequest request = new TradeRequest(caller.getUsername(), toUser, callerIds, targetIds, new Date(), TradeRequestStatus.PENDING, method);
		
		//Update user objects with new Trade Requests
		caller.getTradeRequests().add(request);
		targUsrObj.getTradeRequests().add(request);
		
		//Save to db
		this.userRepo.save(caller);
		this.userRepo.save(targUsrObj);
		
		return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_SUCCESS);
	}
	
	public ServerMessage respondToTradeRequest(User caller) {
		//TODO: Implement
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
