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
import database_entities.User;

@Controller
@RequestMapping(value="/trade/**")
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
	
	//	public ServerMessage sendTradeRequestToUser(User caller, String toUser, List<String> callerIds, List<String> targetIds, String tradeMethodFrom, String tradeMethodTo, boolean counterRequest) {
	@RequestMapping(value="/sendTradeRequest", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage sendTradeRequest(
			@RequestParam(required=true, value="toUser") String toUser,
			@RequestParam(required=true, value="fromIds") String fromIdsJson,
			@RequestParam(required=true, value="toIds") String toIdsJson,
			@RequestParam(required=true, value="tradeMethodFrom") String tradeMethodFrom,
			@RequestParam(required=true, value="tradeMethodTo") String tradeMethodTo,
			@RequestParam(required=true, value="counterRequest") boolean counterRequest,
			HttpServletRequest request
			) {
		
		User caller = SessionHandler.getUserForSession(request);
		if(caller == null) {
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.LOGOUT_FAILED_SESSION);
		}
		
		List<String> fromIds = gson.fromJson(fromIdsJson, new TypeToken<List<String>>(){}.getType());
		if(fromIds == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
		List<String> toIds = gson.fromJson(toIdsJson, new TypeToken<List<String>>(){}.getType());
		if(toIds == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);

		return tradeService.sendTradeRequestToUser(caller, toUser, fromIds, toIds, tradeMethodFrom, tradeMethodTo, counterRequest);
	}

}
