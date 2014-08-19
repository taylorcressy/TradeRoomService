package http_controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import database_entities.TradeItem;
import database_entities.User;
import service.AccountCredentialService;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;
import service.TradeItemService;

@Controller
@RequestMapping(value = "/user/items/**")
public class TradeItemController {

	private static final Logger log = LoggerFactory.getLogger("controller-log");
	
	@Autowired
	private AccountCredentialService accountService;

	@Autowired
	private TradeItemService itemService;

	/**
	 * Add trade item to the currently logged in user
	 * 
	 * All lists will be expected in JSON format
	 * 
	 * @return Server Message
	 */
	@RequestMapping(value = "/addTradeItem", method = RequestMethod.POST)
	public @ResponseBody
	ServerMessage addTradeItemForUser(@RequestParam(required = true, value = "name") String name,
			@RequestParam(required = false, value = "description") String description,
			@RequestParam(required = false, value = "tags") String tagsJson, 
			@RequestParam(required = true, value = "count") Integer count,
			@RequestParam(required = true, value = "condition") String condition, 
			HttpServletRequest request) {
		// First authenticate
		User user = SessionHandler.getUserForSession(request);

		if (user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);

		// Check if user is within Item limit, if not return status
		if (user.getTradeRoomMeta().getItemIds() != null) {
			if (user.getTradeRoomMeta().getItemIds().size() >= user.getTradeRoomMeta().getMaxTradeItemCount()) {
				// User exceeded max limit for adding trade items
				return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_FAILED_MAX_LIMIT);
			}
		}

		List<String> tags = new Gson().fromJson(tagsJson, new TypeToken<List<String>>() {
		}.getType());

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		ServerMessage message = itemService.addTradeItem(name, description, tags, null, count, condition, format.format(new Date()), user.getId());

		if (message.getCode() == StatusMessagesAndCodesService.TRADE_ITEM_CREATION_SUCCESS) {
			// Add item reference to the user
			if (accountService.addTradeItemToUser(user, (TradeItem) message.getData()))
				return message;
			else {
				// Remove the item, then indicate Database Error, Ideally should
				// never get here
				itemService.removeTradeItem(((TradeItem) message.getData()).getId());
				return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
			}
		} else
			return message;
	}
	
	/**
	 * Retrieve a list of TradeItem Data
	 * 
	 * @param itemIds - JSON String of a list of itemIds
	 * @return ServerMessage
	 */
	@RequestMapping(value = "/retrieveItemsFromList", method = RequestMethod.GET)
	public @ResponseBody
	ServerMessage retrieveItemsFromList(
			@RequestParam(value = "itemIds", required = true) String jsonIds,
			HttpServletRequest request
			) {
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);

		List<String> ids = new Gson().fromJson(jsonIds, new TypeToken<List<String>>(){}.getType());
		
		if(ids == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
		
		return itemService.getAllTradeItems(ids);
	}
	
	/**
	 * Update Trade Item Information
	 * 
	 * @param itemId
	 * @param name
	 * @param description
	 * @param tags
	 * @param count
	 * @param condition
	 */
	@RequestMapping(value ="/updateTradeItem", method = RequestMethod.POST)
	public @ResponseBody
	ServerMessage updateTradeItem(
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(required = false, value = "name") String name,
			@RequestParam(required = false, value = "description") String description,
			@RequestParam(required = false, value = "tags") String tagsJson, 
			@RequestParam(required = false, value = "count") Integer count,
			@RequestParam(required = false, value = "condition") String condition, 
			HttpServletRequest request
			) {
		//Authenticate
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		List<String> tags = null;
		if(tagsJson != null) {
			tags = new Gson().fromJson(tagsJson, new TypeToken<List<String>>(){}.getType());
			if(tags == null)
				return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.JSON_PARSE_ERROR);
		}
			
		return itemService.updateTradeItem(user.getId(), itemId, name, description, tags, count, condition);
	}
	
	/**
	 * Delete the trade item associated with the itemId
	 * 
	 * 
	 */
	@RequestMapping(value = "/removeTradeItem", method = RequestMethod.POST)
	public @ResponseBody
	ServerMessage removeTradeItem(
			@RequestParam(value = "itemId", required = true) String itemId, 
			HttpServletRequest request
			) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
	
		ServerMessage message = itemService.removeTradeItem(user.getId(), itemId);
		
		if(message.getCode() == StatusMessagesAndCodesService.DELETE_ITEM_SUCCESS) {
			accountService.removeTradeItemFromUser(user, itemId);
			return message;
		}
		else return message;
	}
	
	
	
	/*
	 * Image Handling
	 */

	/**
	 * Add an image to the user's Trade Item
	 * 
	 * 
	 */
	@RequestMapping(value = "/addImageToTradeItem", method = RequestMethod.POST)
	public @ResponseBody
	ServerMessage addImageToTradeItem(@RequestParam(value = "imageData", required = true) MultipartFile imageData,
			@RequestParam(value = "itemId", required = true) String itemId, HttpServletRequest request) {

		// First authenticate
		User user = SessionHandler.getUserForSession(request);

		if (user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		try {
			return itemService.addImageToTradeItem(user.getId(), itemId, imageData.getBytes());
		}
		catch(IOException ioe) {
			log.error("IO Error: " + ioe.getLocalizedMessage());
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.UNKNOWN_SERVER_ERROR);
		}
	}
	
	/**
	 * Retrieve all images associated with a trade item
	 * 
	 * @param ItemID
	 */
	@RequestMapping(value = "/getImageById", method = RequestMethod.GET, produces = "image/jpeg;")
	public @ResponseBody
	byte[] getImageById(
			@RequestParam(value = "imageId", required = true) String imageId,
			HttpServletRequest request, HttpServletResponse response
			) {
		User user = SessionHandler.getUserForSession(request);
				
		if(user == null) {
			System.out.println("No Session Error in Get Image By ID");
			return "No Session Error".getBytes();
		}
				
		byte[] image = itemService.getImageForTradeItem(imageId);
		
		if(image != null)
			response.setContentLength(image.length);

		return image;
	}

	
	/**
	 * Remove a trade item image from an item
	 * 
	 * @param itemId
	 * @param imageId
	 * @return ServerMessage
	 */
	@RequestMapping(value = "/removeImageById", method = RequestMethod.POST)
	public @ResponseBody
	ServerMessage removeImageById( 
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "imageId", required = true) String imageId,
			HttpServletRequest request
			) {
	
		User user = SessionHandler.getUserForSession(request);
	
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
	
		return itemService.removeTradeItemImage(user.getId(), itemId, imageId);
	}
	
}
