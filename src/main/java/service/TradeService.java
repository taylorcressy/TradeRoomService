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

import database_entities.TradeRequest;
import database_entities.TradeRequest.TradeMethod;
import database_entities.User;
import database_entities.TradeRequest.TradeRequestStatus;
import database_entities.repositories.TradeItemRepository;
import database_entities.repositories.UserRepository;


@Service
public class TradeService {

	
	public static final Logger log = LoggerFactory.getLogger("service-log");
	
	@Autowired
	private StatusMessagesAndCodesService messageService;
	
	@Autowired private UserRepository userRepo;
	
	@Autowired private TradeItemRepository itemRepo;
	
	private Gson gson;
	
	public TradeService() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
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
	public ServerMessage sendTradeRequestToUser(User caller, String toUser, List<String> callerIds, List<String> targetIds, String tradeMethodFrom, String tradeMethodTo, boolean counterRequest) {
		
		User targUsrObj = userRepo.findOneByUsername(toUser);
		
		if(targUsrObj == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_NO_USER);
		
		/*
		 * 	Ensure that all the items given are contained by the appropriate User Object
		 */
		List<String> callerCurrentItems = caller.getTradeRoomMeta().getItemIds();
		List<String> targetCurrentItems = targUsrObj.getTradeRoomMeta().getItemIds();
		
		//Check if the sizes are okay to iterate through. If targets item size is less then the objects, then we know
		//	that there is ids in the request that do not belong
		if(targetCurrentItems.size() < targetIds.size())
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		if(callerCurrentItems.size() < callerIds.size())
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		
		//Check for mismatches
		for(String nextId: callerIds) {
			if(callerCurrentItems.contains(nextId) == false)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS); 
		}
		for(String nextId: targetIds) {
			if(targetCurrentItems.contains(nextId) == false)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		}
		
		//All IDs are valid from this point forward, so create the trade request and save it to both users
		TradeMethod methodFrom = TradeMethod.valueOf(tradeMethodFrom);
		TradeMethod methodTo = TradeMethod.valueOf(tradeMethodTo);
		
		if(methodFrom == null || methodTo == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_TRADE_METHOD);
		
		//Create the request
		TradeRequest request = new TradeRequest(caller.getUsername(), toUser, callerIds, targetIds, new Date(), TradeRequestStatus.PENDING, methodTo, methodFrom, counterRequest);
		
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
