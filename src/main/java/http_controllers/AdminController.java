package http_controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import service.ServerMessage;
import database_entities.User;
import database_entities.User.UserRole;

@Controller
@RequestMapping(value="/admin/**")
public class AdminController {
	
	public AdminController() {

	}

	@RequestMapping(value = "/getUsersWithUsername", method = RequestMethod.POST)
	public @ResponseBody
	ServerMessage getUsersWithUsername(
			@RequestParam(value = "username", required = true) String username,
			HttpServletRequest request
			) {
		
		User user = isAdminSessionAuthenticated(request);
		
		if(user == null) {
			//return un authenticated
		}
		
		return null;
	}
	
	
	/**
	 * Checks to see if the logged in user is logged in and a verified Administrator
	 * @param Http Request
	 * @return User
	 */
	private User isAdminSessionAuthenticated(HttpServletRequest request) {
		
		User user = SessionHandler.getUserForSession(request);
		
		if(user == null)
			return null;
		
		if(user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.ROOT)
			return null;
		
		return user;
	}
	
}
