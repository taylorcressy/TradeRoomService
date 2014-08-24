package http_controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import service.AccountCredentialService;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;
import service.TradeService;
import database_entities.TradeRequest.TradeMethod;
import database_entities.TradeRequest.TradeRequestStatus;
import database_entities.User;

@Controller
@RequestMapping(value="/user/trade/**")
public class TradeController {

	private static final Logger log = LoggerFactory.getLogger("controller-log");
	
	@Autowired
	private AccountCredentialService accountService;
	
	@Autowired
	private TradeService tradeService;
	
	private Gson gson;
	
	public TradeController() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	

	@RequestMapping(value="/getTradeRequestsOfLoggedInUser", method=RequestMethod.GET)
	public @ResponseBody
	ServerMessage getTradeRequestsOfLoggedInUser(HttpServletRequest request) {
		User caller = SessionHandler.getUserForSession(request);
		
		if(caller == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);

		return tradeService.getTradeRequestsForAllIds(caller.getTradeRequests());
	}
	
	@RequestMapping(value="/sendTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage sendTradeRequest(
			@RequestParam(required=true, value="toUser") String toUser,
			@RequestParam(required=false, value="fromIds") String fromIdsJson,
			@RequestParam(required=false, value="toIds") String toIdsJson,
			@RequestParam(required=true, value="tradeMethod") String tradeMethod,
			@RequestParam(required=false, value="message") String message,
			@RequestParam(required=true, value="counterRequest") boolean counterRequest,
			HttpServletRequest request
			) {
		
		User caller = SessionHandler.getUserForSession(request);
		if(caller == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		}
		
		if(fromIdsJson == null && toIdsJson == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_NO_ITEMS);
		
		List<String> fromIds = gson.fromJson(fromIdsJson, new TypeToken<List<String>>(){}.getType());
		if(fromIds == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
		List<String> toIds = gson.fromJson(toIdsJson, new TypeToken<List<String>>(){}.getType());
		if(toIds == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);

		return tradeService.sendTradeRequestToUser(caller, toUser, fromIds, toIds, tradeMethod, message, counterRequest);
	}

	
	@RequestMapping(value="/respondToTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage respondtoTradeRequest(
			@RequestParam(value="requestId", required=true) String requestId,
			@RequestParam(value="status", required=true) String status,
			HttpServletRequest request
			) {
		
		User caller = SessionHandler.getUserForSession(request);
		
		if(caller == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		TradeRequestStatus requestStatus = TradeRequestStatus.valueOf(status);
		if(requestStatus == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.RESP_TRADE_REQ_FAILED_INVALID_STATUS);
		
		return tradeService.respondToTradeRequest(caller, requestId, requestStatus);	
	}
	
	@RequestMapping(value="/receivedTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage receivedTradeRequest (
			@RequestParam(value="requestId", required=true) String requestId,
			HttpServletRequest request
			) {
		User caller = SessionHandler.getUserForSession(request);
		
		if(caller == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		return tradeService.markTradeRequestAsReceived(caller, requestId);
	}
	
	@RequestMapping(value="/counterTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage counterTradeReqeust(
			@RequestParam(value="requestId", required = true) String requestId,
			@RequestParam(value="method", required = true) String method,
			@RequestParam(value="fromIds", required = false) String fromItemsJson,
			@RequestParam(value="toIds", required = false) String toItemsJson,
			@RequestParam(value="message", required = false) String message,
			HttpServletRequest request
			)
	{
		User caller = SessionHandler.getUserForSession(request);
		
		if(caller == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		TradeMethod tradeMethod = TradeMethod.valueOf(method);
		if(tradeMethod == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SEND_TRADE_REQ_FAIL_TRADE_METHOD);
		
		List<String> fromItems = this.gson.fromJson(fromItemsJson, new TypeToken<List<String>>(){}.getType());
		if(fromItems == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
		
		List<String> toItems = this.gson.fromJson(toItemsJson, new TypeToken<List<String>>(){}.getType());
		if(toItems == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
		
		return tradeService.counterTradeRequest(caller, requestId, tradeMethod, message, fromItems, toItems);
	}
	
	@RequestMapping(value="/cancelTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage cancelTradeRequest(
		@RequestParam(value="requestId", required = true) String requestId,
		HttpServletRequest request
			) {
		User caller = SessionHandler.getUserForSession(request);
		if(caller == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		return tradeService.cancelTradeRequest(caller, requestId);
	}
	
	@RequestMapping(value="/clearTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage clearTradeRequest(
			@RequestParam(value="requestId", required=true) String requestId,
			HttpServletRequest request
			) {
		User caller = SessionHandler.getUserForSession(request);
		if(caller == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		return tradeService.clearTradeRequest(caller, requestId);
	}
}