/**
 * Resource Controller
 * 
 * This class is responsible for servicing all users with specific forms of data. 
 * This can include downloading non-session related material: Lists, CSVs, MOTDs etc...
 * 
 * @author TaylorCressy
 */
package http_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database_entities.TradeItem;
import database_entities.TradeRequest;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;

@Controller
@RequestMapping("/resource/**")
public class ResourceController {

	@Autowired
	private StatusMessagesAndCodesService messageService;
		
	public ResourceController() {
	}
	
	@RequestMapping(value = "/getConditionsList", method = RequestMethod.GET)
	public @ResponseBody
	ServerMessage getConditionsList() {
		
		String [] conditions = TradeItem.ItemCondition.names();

		return messageService.getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, conditions);
	}
	
	@RequestMapping(value = "/getTradeOptions", method = RequestMethod.GET)
	public @ResponseBody
	ServerMessage getTradeOptions() {
		String [] tradeOptions = TradeRequest.TradeMethod.names();
		
		return messageService.getMessageWithData(StatusMessagesAndCodesService.GET_REQUEST_SUCCESS, tradeOptions);
	}
}
