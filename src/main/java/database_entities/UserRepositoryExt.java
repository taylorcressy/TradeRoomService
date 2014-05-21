/**
 * Custom implementation for the UserRepository. This interface defines the API for 
 * additional functionality for the UserRepository.
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @Date 21 April, 2014
 */
package database_entities;

import java.util.List;

import database_entities.exceptions.FatalDatabaseErrorException;
import database_entities.exceptions.FriendRequestDoesNotExistException;
import database_entities.exceptions.FriendRequestExistsException;
import database_entities.exceptions.UserObjectNotInitializedForOperation;

public interface UserRepositoryExt {

	/* User Operations */
	public List<User> findMultipleUsersByUsername(List<String> usernames);

	public List<User> findMultipleUsersByEmail(List<String> emails);

	public boolean deleteUserByUsername(String username);

	public boolean deleteUserByEmail(String email);

	
	/* Account Preference Operations */
	public boolean updateAccountPreferences(User user, AccountPreference acc);

	public boolean updateAddress(User user, Address address);

	public boolean updateCurrentGeoLocation(User user, String location);

	public boolean updatePreferredLocations(User user, List<Address> addressList);

	
	/* Ranking Operations */
	public boolean addRank(User user, Rank rank);

	public boolean deleteRank(User user, String id);

	
	/* Friend Request operations */
	public boolean sendFriendRequest(User from, String toUsername) throws FriendRequestExistsException, FatalDatabaseErrorException;

	public boolean acceptFriendRequest(User caller, String friendUsername) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation;

	public boolean denyFriendRequest(User caller, String friendUsername) throws FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation;

	public boolean blockFriendRequest(User caller, String friendUsername) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation;

	
	/* Trade Request operations */
}
