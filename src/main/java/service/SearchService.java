/**
 * Service logic handling for all search related queries
 */
package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database_entities.TradeItem;
import database_entities.User;
import database_entities.repositories.TradeItemRepository;
import database_entities.repositories.UserRepository;

@Service
public class SearchService {

	@Autowired private StatusMessagesAndCodesService messagingService;
	@Autowired private UserRepository userRepo;
	@Autowired private TradeItemRepository itemRepo;
	
	private Gson gson;
	public SearchService() {
		this.gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
	}
	
	/**
	 * Search for Users based on a likewise query of usernames
	 * @param search
	 * @return
	 */
	public ServerMessage searchByUsername(User caller, String search) {
		List<User> users = userRepo.findAllByUsernameLike(search);
		
		if(users == null)
			return messagingService.getMessageForCode(StatusMessagesAndCodesService.SEARCH_FAILED);
		
		List<String> userJsons = new ArrayList<String>();
		for(User user: users) {
			if(caller.getId().compareTo(user.getId()) != 0)
				userJsons.add(this.gson.toJson(FriendsService.screenUser(user)));
		}
		
		return messagingService.getMessageWithData(StatusMessagesAndCodesService.SEARCH_SUCCESSFUL, userJsons) ;
	}
	
	public ServerMessage searchForTradeItemsTextIndex(String query, User caller) {
		List<TradeItem> items = itemRepo.findItemsWithTextIndexedSearchAndOwnerIdNot(query, caller.getId());
		
		if(items == null)
			return messagingService.getMessageForCode(StatusMessagesAndCodesService.SEARCH_FAILED);
		
		return messagingService.getMessageWithData(StatusMessagesAndCodesService.SEARCH_SUCCESSFUL, items);
	}
}
