/**
 * Static class providing session management for all controller classes.
 * 
 * 
 */
package http_controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import database_entities.User;

public class SessionHandler {

	private static final String USER_SESSION_KEY = "TradeRoomUserSession";

	
	/**
	 * Retrieve an account for a given Session
	 * 
	 * @param HttpServletRequest
	 * @return User
	 * @return null - No Session set for the user
	 */
	public static User getUserForSession(HttpServletRequest request) {
		if(request == null)
			return null;
		
		HttpSession session = request.getSession();
		User user = null;
		if(session != null) {
			Object obj;
			obj = session.getAttribute(USER_SESSION_KEY);
			if(obj instanceof User) {
				user = (User) obj;
			}
			else return null;
		}
		
		return user;
	}
	
	/**
	 * Set the current user to the current session
	 * 
	 * @param request
	 * @param user
	 * @return boolean
	 */
	public static boolean setUserToSession(HttpServletRequest request, User user) {
		if(request == null)
			return false;
		
		HttpSession session = request.getSession();
		if(session == null)
			return false;
		
		if(user == null) {
			session.removeAttribute(USER_SESSION_KEY);
			return false;
		}
		else {
			session.setAttribute(USER_SESSION_KEY, user);
			return true;
		}
	}
}
