/**
 * Service taking care of all aspects associated with a trade room. This means that it will handle 
 * screening items to other user's and facilitating Trade Requests
 */
package service;

import java.util.ArrayList;
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
import database_entities.repositories.TradeRequestRepository;
import database_entities.repositories.UserRepository;


@Service
public class TradeService {

	
	public static final Logger log = LoggerFactory.getLogger("service-log");
	
	@Autowired
	private StatusMessagesAndCodesService messageService;
	
	@Autowired private UserRepository userRepo;
	
	@Autowired private TradeItemRepository itemRepo;
	
	@Autowired private TradeRequestRepository requestRepo;
	
	private Gson gson;
	
	public TradeService() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	
	/**
	 * Get the trade request associated with a user id
	 */
	public ServerMessage getTradeRequestForId(String id) {
		if(id == null)
			return null;
		
		TradeRequest request = requestRepo.findOne(id);
		
		if(request == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.GET_REQUEST_FAILED);
		else
			return messageService.getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, request);
	}
	
	/**
	 * Get all trade requests associated with all of the ids in the given list
	 */
	public ServerMessage getTradeRequestsForAllIds(List<String> ids) {
		if(ids == null)
			return null;
		
		List<TradeRequest> requests = (List<TradeRequest>) requestRepo.findAll(ids);
		
		if(requests == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.GET_REQUEST_FAILED);
		else 
			return messageService.getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, requests);
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
	public ServerMessage sendTradeRequestToUser(User caller, String toUser, List<String> callerIds, List<String> targetIds, String tradeMethod, String message, boolean counterRequest) {
		
		User targUsrObj = userRepo.findOne(toUser);
		
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
		TradeMethod method = TradeMethod.valueOf(tradeMethod);
		
		if(method == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_TRADE_METHOD);
		
		//Create the request
		TradeRequest request = new TradeRequest(caller.getId(), toUser, callerIds, targetIds, new Date(), TradeRequestStatus.PENDING, method, counterRequest);
		request.setMessage(message);
		//Now we need to ensure that duplicate Trade Requests aren't made. To do that, use Java's built in Sorting algorithms,
		// then check if arrays are equal to the Trade Requests already in the user objects
		//First get the Trade Requests
		Iterable<TradeRequest> callerTradeRequests = requestRepo.findAll(caller.getTradeRequests());
		
		//Then compare
		for(TradeRequest existingRequest: callerTradeRequests) {
			if(existingRequest.isSame(request))
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_SAME_REQUEST);
		}
		
		//Update user objects with new Trade Requests
		if(caller.getTradeRequests() == null)
			caller.setTradeRequests(new ArrayList<String>());
		if(targUsrObj.getTradeRequests() == null)
			targUsrObj.setTradeRequests(new ArrayList<String>());
		
		TradeRequest savedRequest = requestRepo.save(request);
		
		caller.getTradeRequests().add(savedRequest.getId());
		targUsrObj.getTradeRequests().add(savedRequest.getId());
		
		//Update the users
		List<User> bulk = new ArrayList<User>();
		bulk.add(caller);
		bulk.add(targUsrObj);
		this.userRepo.save(bulk);		
		
		return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_SUCCESS);
	}
	
	/**
	 * Respond to the trade request presented to the caller
	 * 
	 * The status options are ACCEPTED / DECLINED
	 * Counters will be handled separately, as they follow a bit different logic
	 * 
	 * @param caller
	 * @param requestId
	 * @param status
	 * @return ServerMessage
	 */
	public ServerMessage respondToTradeRequest(User caller, String requestId, TradeRequestStatus status) {
		//First retrieve the reference to the TradeRequest
		TradeRequest request = requestRepo.findOne(requestId);
		
		if(request == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_REQ_NON_EXISTENT);
		
		//Ensure that this is a pending request
		if(request.getStatus() != TradeRequestStatus.PENDING)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_REQ_NOT_PENDING);
		
		//Ensure that it is the target of a request that is replying
		if(request.getTo().compareTo(caller.getId()) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_FAILED_USER_NOT_TARGET);
		
		//Check to ensure the TradeRequest is owned by the caller
		if(request.getFrom().compareTo(caller.getId()) != 0 &&
				request.getTo().compareTo(caller.getId()) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_FAILED_WRONG_OWNER);
		
		//Check to ensure that this is the status is accurate
		if(status != TradeRequestStatus.ACCEPTED && status != TradeRequestStatus.DECLINED)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_FAILED_INVALID_STATUS);

		//Update the request
		request.setStatus(status);
		requestRepo.save(request);
		
		return messageService.getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_SUCCESS);
	}
	
	/**
	 * Counter the Trade Request. The from item ids are the original from ids. So they will be set to the "to ids" when it is countered.
	 * 
	 * @param caller
	 * @param requestId
	 * @param fromItemIds
	 * @param toItemIds
	 * @return ServerMessage
	 */
	public ServerMessage counterTradeRequest(User caller, String requestId, TradeMethod method, String message, List<String> fromItemIds, List<String> toItemIds) {
		//Preliminary ensure that at least one of the lists have items in it
		if((fromItemIds == null && toItemIds == null) || (fromItemIds.size() == 0 && toItemIds.size() == 0))
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_NO_ITEMS);
		
		//First retrieve the reference of the tradeRequest
		TradeRequest request = requestRepo.findOne(requestId);
		
		if(request == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_REQ_NON_EXISTENT);
		
		//Then ensure that the status of the request is correct
		if(request.getStatus() != TradeRequestStatus.PENDING)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_FAILED_INVALID_STATUS);
		
		//Check to ensure the TradeRequest is owned by the caller
		if(request.getFrom().compareTo(caller.getId()) != 0 &&
				request.getTo().compareTo(caller.getId()) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_FAILED_WRONG_OWNER);
		
		//Then ensure that the items are owned by appropriate user
		//Start by grabbing the original from (the new target)
		User newTarget = userRepo.findOne(request.getFrom());
		if(newTarget == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_NO_USER);

		//Grab there items ids
		List<String> newTargetItems = newTarget.getTradeRoomMeta().getItemIds();
		List<String> newFromItems = caller.getTradeRoomMeta().getItemIds();
		
		//Check if the sizes are right. (sort of optimizing the comparison, avoids if necessary)
		if(newTargetItems.size() < toItemIds.size() ||
				newFromItems.size() < fromItemIds.size())
			return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);

		//Finally iterate through and see if it is contained in the respective User's Trade Room
		for(String id: fromItemIds) {
			if(!newFromItems.contains(id))
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		}
		for(String id: toItemIds) {
			if(!newTargetItems.contains(id))
				return messageService.getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_INVALID_ITEMS);
		}
		
		//Finally update the Trade Request
		request.setFromItems(fromItemIds);
		request.setToItems(toItemIds);
		request.setCounterRequest(true);
		request.setMethod(method);
		request.setMessage(message);
		
		//Then save the request 
		TradeRequest success = requestRepo.save(request);
		if(success == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		else
			return messageService.getMessageForCode(StatusMessagesAndCodesService.COUNTER_TRADE_REQ_SUCCESS);
	}
	
	/**
	 * Mark an accepted trade as received by the caller. For now, this simply marks the appropriate boolean as true in the TradeRequest
	 * object so that the client can adequately deal with how this is handled. 
	 * 
	 * In the future though, we will want to move these to a separate collection. (only when both user's have marked a request as received.
	 * 
	 * @param caller
	 * @param requestId
	 * @return
	 */
	public ServerMessage markTradeRequestAsReceived(User caller, String requestId) {
		//Retrieve request
		TradeRequest request = requestRepo.findOne(requestId);
		if(request == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_REQ_NON_EXISTENT);
		
		//Ensure the proper status
		if(request.getStatus() != TradeRequestStatus.ACCEPTED)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.MARK_TRADE_REQ_FAILED_INVALID_STATUS);
		
		//Ensure the owner
		boolean isFrom = (request.getFrom().compareTo(caller.getId()) == 0);
		boolean isTo = (request.getTo().compareTo(caller.getId()) == 0);
		if(!isFrom & !isTo)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.MARK_TRADE_REQ_FAILED_INVALID_USER);
		
		if(isFrom) {
			if(request.isReceivedBySource())
				return messageService.getMessageForCode(StatusMessagesAndCodesService.MARK_TRADE_REQ_FAILED_ALREADY_MARKED);
			else {
				request.setReceivedBySource(true);
				requestRepo.save(request);
				return messageService.getMessageForCode(StatusMessagesAndCodesService.MARK_TRADE_REQ_READ_SUCCESS);
			}
		}
		else {
			//Has to be isTo
			if(request.isReceivedByTarget())
				return messageService.getMessageForCode(StatusMessagesAndCodesService.MARK_TRADE_REQ_FAILED_ALREADY_MARKED);
			else {
				request.setReceivedByTarget(true);
				requestRepo.save(request);
				return messageService.getMessageForCode(StatusMessagesAndCodesService.MARK_TRADE_REQ_READ_SUCCESS);
			}
		}		
	}
	
	/**
	 * Cancel a Trade Request. Can only be called by the sender of a pending request. Or by either user's of an accepted request.
	 * 
	 * @param caller
	 * @param requestId
	 * @return
	 */
	public ServerMessage cancelTradeRequest(User caller, String requestId) {
		//First get the request
		TradeRequest request = requestRepo.findOne(requestId);
		
		if(request == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_REQ_NON_EXISTENT);
		
		if(request.getStatus() == TradeRequestStatus.PENDING)
		{
			//Check to make sure that it is the caller that is cancelling. Other wise the target should be calling deny
			if(caller.getId().compareTo(request.getFrom()) != 0)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.CANCEL_TRADE_REQ_FAILED_INVALID_USER);
			
			//Delete the request
			requestRepo.delete(request);
			return messageService.getMessageForCode(StatusMessagesAndCodesService.CANCEL_TRADE_REQ_SUCCESS);
		}
		else if(request.getStatus() == TradeRequestStatus.ACCEPTED) 
		{
			//Either user can delete it
			if(caller.getId().compareTo(request.getFrom()) != 0 && caller.getId().compareTo(request.getTo()) != 0)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.CANCEL_TRADE_REQ_FAILED_INVALID_USER);
			//Delete the request
			requestRepo.delete(request);
			return messageService.getMessageForCode(StatusMessagesAndCodesService.CANCEL_TRADE_REQ_SUCCESS);
		}
		else
			return messageService.getMessageForCode(StatusMessagesAndCodesService.CANCEL_TRADE_REQ_FAILED_INVALID_STATUS);
	}
	
	/**
	 * Clear a Trade Request. This is specific to denied Trade Requests. So the client should display when a another user has denied a trade request,
	 * 
	 * The user then has the option to clear it from their list. That means, the person who initiated a declined will still receive a reference to the declined
	 * Trade Request. It is up to the client to implement logic not to show Declines that it initiated.
	 * 
	 * Clearing simply deletes the Trade Request from the database
	 */
	public ServerMessage clearTradeRequest(User caller, String requestId) {
		//First get the request
		TradeRequest request = this.requestRepo.findOne(requestId);
		
		if(request == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_REQ_NON_EXISTENT);
		
		//Only a target of a declined can clear
		if(request.getStatus() == TradeRequestStatus.DECLINED) {
			if(caller.getId().compareTo(request.getFrom()) != 0)
				return messageService.getMessageForCode(StatusMessagesAndCodesService.CLEAR_TRADE_REQ_FAILED_INVALID_USER);
			
			//Delete the message
			requestRepo.delete(request);
			return messageService.getMessageForCode(StatusMessagesAndCodesService.CLEAR_TRADE_REQ_SUCCESS);
		}
		else 
			return messageService.getMessageForCode(StatusMessagesAndCodesService.CLEAR_TRADE_REQ_FAILED_INVALID_STATUS);
	}
	
}
