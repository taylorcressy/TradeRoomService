/**
 * The HTTP front end for searching for users and items
 */
package http_controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import database_entities.User;
import service.AccountCredentialService;
import service.SearchService;
import service.ServerMessage;
import service.StatusMessagesAndCodesService;

@Controller
@RequestMapping(value="/search/**")
public class SearchController {

	@Autowired private AccountCredentialService accountService;
	@Autowired private SearchService searchService;
	
	
	@RequestMapping(value="/searchForUser", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage searchForUserWithQuery(
			@RequestParam(value="query", required=true) String query,
			HttpServletRequest request
			) {
		
		if(query.length() == 0)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SEARCH_EMPTY);
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		 return searchService.searchByUsername(user, query);
	}
	
	@RequestMapping(value="/searchForItem", method=RequestMethod.POST)
	public @ResponseBody
	ServerMessage searchForItemWithQuery(
			@RequestParam(value="query", required=true) String query,
			HttpServletRequest request
			) {
		
		if(query.length() == 0)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SEARCH_EMPTY);
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return accountService.getMessagingService().getMessageForCode(StatusMessagesAndCodesService.SESSION_NON_EXISTENT);
		
		 return searchService.searchForTradeItemsTextIndex(query, user);
	}
}
