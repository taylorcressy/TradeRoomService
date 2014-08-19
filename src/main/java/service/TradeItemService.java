package service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import server_utilities.DefaultProperties;
import database_entities.TradeItem;
import database_entities.TradeItem.ItemCondition;
import database_entities.repositories.TradeItemRepository;

@Service
public class TradeItemService {

	private static final Logger log = LoggerFactory.getLogger("service-logger");

	@Autowired
	private StatusMessagesAndCodesService messageService;

	@Autowired
	private TradeItemRepository itemRepo;
	
	@Autowired
	private DefaultProperties defaultProperties;

	/**
	 * Add a trade room item. Returns the TradeItem Object to the caller.
	 * 
	 * @param name
	 * @param description
	 * @param geoLocation
	 * @param tags
	 * @param imageIds
	 * @param count
	 * @param condition
	 * @param dateAdded
	 * @param ownerId
	 * @return ServerMessage
	 */
	public ServerMessage addTradeItem(String name, String description, List<String> tags, List<String> imageIds, Integer count, String condition,
			String dateAdded, String ownerId) {
		if (name == null || name=="" || condition == null || condition=="" || dateAdded == null || ownerId == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_FAILED_FORM);
				
		if (name.trim().length() < defaultProperties.getIntProperty("minItemNameSize")
				|| name.trim().length() > defaultProperties.getIntProperty("maxItemNameSize"))
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_FAILED_FORM);
		
		if(description != null && description.length() > defaultProperties.getIntProperty("maxItemDescriptionSize"))
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_FAILED_FORM);
		
		if(tags != null && tags.size() > defaultProperties.getIntProperty("maxNumberOfTags"))
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_FAILED_FORM);
		
		if(imageIds != null && imageIds.size() > defaultProperties.getIntProperty("maxNumberOfImages"))
			return messageService.getMessageForCode(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_FAILED_FORM);
		
		if(count > defaultProperties.getIntProperty("maxItemCount"))
			count = defaultProperties.getIntProperty("maxItemCount");
		else if(count <= 0)
			count = 1;
		
		TradeItem item = new TradeItem(name, description, tags, imageIds, count, ItemCondition.valueOf(condition.toUpperCase()), dateAdded, ownerId);
				
		item = itemRepo.save(item);
				
		if(item == null) {
			log.error("Database Error in saving TradeItem");
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		}
		else {
			log.info("Saved item: " + item.toString());
			return messageService.getMessageWithData(StatusMessagesAndCodesService.TRADE_ITEM_CREATION_SUCCESS, item);
		}
	}

	/**
	 * Update a Trade Room Item. The Owner ID must be provided to ensure the
	 * item being updated belongs to the caller.
	 * 
	 * @param ownerId
	 * @param itemId
	 * @return ServerMessage
	 */
	public ServerMessage updateTradeItem(String ownerId, String itemId, String name, String description, List<String> tags, Integer count, String condition) {
		if(ownerId == null || itemId == null)
			throw new IllegalArgumentException("Invalid Ids");
		
		TradeItem item = itemRepo.findOne(itemId);
		
		//Item Does Not Exist
		if(item == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ITEM_FAILED_NO_ITEM);
		
		//Item Does Not Belong To User
		if(item.getOwnerId().compareToIgnoreCase(ownerId) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.UPDATE_ITEM_FAILED_WRONG_OWNER);
		
		if(name != null && name.trim().length() >= defaultProperties.getIntProperty("minItemNameSize") 
				&& name.trim().length() <= defaultProperties.getIntProperty("maxItemNameSize") )
			item.setName(name);
		else if(name != null)	//Invalid name parameter
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_ITEM_FAILED_INVALID_FORM, "name");
		
		if(description != null && description.length() <= defaultProperties.getIntProperty("maxItemDescriptionSize"))
			item.setDescription(description);
		else if(description != null) //Invalid description parameter
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_ITEM_FAILED_INVALID_FORM, "description");
			
		if(tags != null && tags.size() <= defaultProperties.getIntProperty("maxNumberOfTags"))
			item.setTags(tags);
		else if(tags != null)	//Invalid Tags Parameter
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_ITEM_FAILED_INVALID_FORM, "tags");
		
		if(count != null && count < defaultProperties.getIntProperty("maxItemCount") && count > 0)
			item.setCount(count);
		else if(count != null && count > defaultProperties.getIntProperty("maxItemCount"))
			item.setCount(defaultProperties.getIntProperty("maxItemCount"));
		else if(count != null && count <= 0)
			item.setCount(1);
		
		if(condition != null) {
			try {
				ItemCondition itemCondition = ItemCondition.valueOf(condition.toUpperCase());
				item.setCondition(itemCondition);
			}
			catch(Exception e) {	//Failed to convert to ItemCondition type
				return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_ITEM_FAILED_INVALID_FORM, "condition");
			}
		}
		
		TradeItem returnItem = itemRepo.save(item);
		
		if(returnItem == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		else 	//Success
			return messageService.getMessageWithData(StatusMessagesAndCodesService.UPDATE_ITEM_SUCCESS, new Gson().toJson(returnItem));		
	}
	
	/**
	 * Remove a TradeRoomItem. The Owner ID must be provided to ensure the
	 * item being updated belongs to the caller.
	 * 
	 * @param ownerId
	 * @param itemId
	 * @return ServerMessage
	 */
	public ServerMessage removeTradeItem(String ownerId, String itemId) {
		if(ownerId == null || itemId == null)
			throw new IllegalArgumentException("Invalid Ids");
		
		TradeItem item = itemRepo.findOne(itemId);
		
		//Item Does Not Exist
		if(item == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DELETE_ITEM_FAILED_NO_ITEM);
		
		//Item Does Not Belong to User
		if(item.getOwnerId().compareToIgnoreCase(ownerId) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DELETE_ITEM_FAILED_WRONG_OWNER);
		
		if(item.getImageIds() != null) {
			for(String imageId: item.getImageIds()) {
				itemRepo.deleteTradeItemImage(imageId);
			}
		}
		
		
		
		itemRepo.delete(item);
		
		return messageService.getMessageForCode(StatusMessagesAndCodesService.DELETE_ITEM_SUCCESS);
	}
	
	
	/**
	 * Add an image to a specified Trade Item.
	 * 
	 * @param UserId
	 * @param TradeItemId
	 * @param imageBytes
	 * @return ServerMessage
	 */
	public ServerMessage addImageToTradeItem(String userId, String itemId, byte[] imageData) {
		TradeItem item = itemRepo.findOne(itemId);
		
		//Trade Item does not exist (probably a client error)
		if(item == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_SAVE_FAIL_NO_ITEM);
		
		if(item.getOwnerId().compareTo(userId) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_SAVE_FAIL_WRONG_OWNER);
		
		if(item.getImageIds() == null)
			item.setImageIds(new ArrayList<String>());
		if(item.getImageIds().size() >= defaultProperties.getIntProperty("maxNumberOfImages"))
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_SAVE_FAIL_MAX_IMAGES);
		
		String imageId = itemRepo.saveTradeItemImage(imageData, itemId + userId, "itemImage");
		
		if(item.getImageIds() == null)
			item.setImageIds(new ArrayList<String>());
		
		item.getImageIds().add(imageId);
		
		TradeItem success = itemRepo.save(item);
		
		if(success == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
		else
			return messageService.getMessageWithData(StatusMessagesAndCodesService.IMAGE_SAVE_SUCCESS, new Gson().toJson(success));		
	}
	
	/**
	 * Retrieve all item data associated with the list of itemIds
	 * 
	 * @param List<String>
	 * @return ServerMessage
	 */
	public ServerMessage getAllTradeItems(List<String> ids) {
		if(ids == null)
			throw new IllegalArgumentException("Null list");
		
		List<TradeItem> items = new ArrayList<TradeItem>();
		
		TradeItem nextItem;
		for(String next: ids) {
			nextItem = itemRepo.findOne(next);
			if(nextItem != null)
				items.add(nextItem);
		}
		
		//if(items.size() == 0) 
		//	return messageService.getMessageForCode(StatusMessagesAndCodesService.RECV_ITEMS_FAILED_NO_ITEMS);
		//else
			return messageService.getMessageWithData(StatusMessagesAndCodesService.RECV_ITEMS_SUCCESS, items);
		
	}
	
	/**
	 * Retrieve an image associated with the imageId
	 * 
	 * @param imageId
	 * @return byte[]
	 */
	public byte[] getImageForTradeItem(String imageId) {
		if(imageId == null)
			throw new IllegalArgumentException("Null values");
		
		byte[] image = itemRepo.getTradeItemImage(imageId);
		
		System.out.println("Retrieved image of size: " + image.length + "B");
		
		return image;
	}
	
	/**
	 * Remove a Trade Room ItemImage. The Owner ID must be provided to ensure the
	 * item being removed belongs to the caller
	 * 
	 * @param ownerId
	 * @param itemId
	 * @param imageId
	 * @return ServerMessage
	 */
	public ServerMessage removeTradeItemImage(String ownerId, String itemId, String imageId) {
		
		TradeItem item = itemRepo.findOne(itemId);
		
		//Trade Item does not exist
		if(item == null)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_DELETE_FAILED_NO_ITEM);
		
		if(ownerId.compareToIgnoreCase(item.getOwnerId()) != 0)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_DELETE_FAILED_INVALID_USER);
		
		//See if the image exists for the item
		boolean match = false;
		int index = 0;
		if(item.getImageIds() != null) {
			for(String next: item.getImageIds()) {
				if(next.compareToIgnoreCase(imageId) == 0) {
					match = true;
					break;
				}
				index++;
			}
		}
		
		if(!match)
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_DELETE_FAILED_NO_IMAGE);
		
		if(itemRepo.deleteTradeItemImage(imageId)) {
			item.getImageIds().remove(index);
			return messageService.getMessageForCode(StatusMessagesAndCodesService.IMAGE_DELETE_SUCCESS);
		}
		else
			return messageService.getMessageForCode(StatusMessagesAndCodesService.DATABASE_ERROR);
	}
	
	/**
	 * Search for Trade Room Item 
	 */
	
	
	
	/**
	 * Remove a Trade Room Item (internal use only) IMPORTANT: DO NOT EXPOSE THIS TO THE RESTFUL API
	 * 
	 * This is a maintenance function to be used when inconsistencies occur in the DB
	 * @param itemId
	 * @return boolean
	 */
	public boolean removeTradeItem(String itemId) {
		if(itemId == null)
			throw new IllegalArgumentException("Null values");
		
		itemRepo.delete(itemId);
		return true;
	}
}
