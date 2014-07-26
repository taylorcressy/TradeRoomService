/**
 * Custom implementation for the UserRepository. This interface defines the API for 
 * additional functionality for the UserRepository.
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @Date 21 April, 2014
 */
package database_entities;

/*Seriously remove 90% of these functions. Unnecessary for the most part*/

import java.util.List;

import database_entities.exceptions.FatalDatabaseErrorException;
import database_entities.exceptions.FriendRequestDoesNotExistException;
import database_entities.exceptions.FriendRequestExistsException;
import database_entities.exceptions.UserObjectNotInitializedForOperation;

public interface UserRepositoryExt {

	/* User Operations */
	public List<User> findMultipleUsersByUsername(List<String> usernames);

	public List<User> findMultipleUsersByEmail(List<String> emails);

	
	/* Friend Request operations */
	public boolean sendFriendRequest(User from, String toId) throws FriendRequestExistsException, FatalDatabaseErrorException;

	public boolean acceptFriendRequest(User caller, String friendId) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation;

	public boolean denyFriendRequest(User caller, String friendId) throws FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation;

	public boolean blockFriendRequest(User caller, String friendId) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation;

	
	/* Trade Request operations */
}
