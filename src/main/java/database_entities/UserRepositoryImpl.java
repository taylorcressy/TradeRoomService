/**
 * The implementation for UserRepositoryExt. This extends the functionality of a basic Paging Repository
 * 
 * All functions will reflect the change made via the User object passed to each function.
 * 
 * @author Taylor Cressy
 * @version 1.0
 * @date 22 April 2014
 * 
 * TODO: ADD LOGGING!!!!!
 */
package database_entities;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

import database_entities.FriendRequest.FriendRequestStatus;
import database_entities.exceptions.FatalDatabaseErrorException;
import database_entities.exceptions.FriendRequestDoesNotExistException;
import database_entities.exceptions.FriendRequestExistsException;
import database_entities.exceptions.UserObjectNotInitializedForOperation;

public class UserRepositoryImpl implements UserRepositoryExt {

	private static final Logger log = LoggerFactory.getLogger("database-logger");

	@Autowired
	private MongoTemplate operations;

	/**
	 * Find all users associated with the username list provided
	 * 
	 * @param List<String>
	 * @return List<User>
	 */
	public List<User> findMultipleUsersByUsername(List<String> usernames) {
		if(usernames == null || usernames.size() <= 0)
			throw new IllegalArgumentException("List of usernames is null or undefined");
		
		Criteria criteria = Criteria.where("username").in(usernames);
		
		return operations.find(new Query(criteria), User.class);
	}
	
	/**
	 * Find all users associated with the email list provided
	 * 
	 * @param List<String>
	 * @return List<User>
	 */
	public List<User> findMultipleUsersByEmail(List<String> emails) {
		if(emails == null || emails.size() <= 0)
			throw new IllegalArgumentException("List of emails is null or undefined");
		
		Criteria criteria = Criteria.where("email").in(emails);
		
		return operations.find(new Query(criteria), User.class);
	}
	
	@Override
	public boolean deleteUserByUsername(String username) {
		if(username == null)
			throw new IllegalArgumentException("username can't be null");
		
		operations.remove(new Query(Criteria.where("username").is(username)), User.class);
		
		if(operations.getDb().getLastError().getInt("n") == 1)
			return true;
		else
			return false;
	}
	
	@Override
	public boolean deleteUserByEmail(String email) {
		if(email == null)
			throw new IllegalArgumentException("email can't be null");
		
		operations.remove(new Query(Criteria.where("email").is(email)), User.class);
		
		if(operations.getDb().getLastError().getInt("n") == 1)
			return true;
		else
			return false;
	}
	
	/**
	 * Update the user's account preferences in the DB. NOTE: The user's address
	 * will not be altered and should be handled separately. NOTE: The user's
	 * current location will be handled separately NOTE: The user's preferred
	 * meeting locations will be handled separately
	 */
	@Override
	public boolean updateAccountPreferences(User user, AccountPreference pref) {
		if (pref == null || user == null)
			throw new IllegalArgumentException("Null Values");

		AccountPreference combinedPref = new AccountPreference();
		
		if (pref.getDob() != null)
			combinedPref.setDob(pref.getDob());
		else
			combinedPref.setDob(user.getAccountPreference().getDob());

		if (pref.getFirstName() != null)
			combinedPref.setFirstName(pref.getFirstName());
		else
			combinedPref.setFirstName(user.getAccountPreference().getFirstName());

		if (pref.getLastName() != null)
			combinedPref.setLastName(pref.getLastName());
		else
			combinedPref.setLastName(user.getAccountPreference().getLastName());

		if (pref.getPasswordHash() != null)
			combinedPref.setPasswordHash(pref.getPasswordHash());
		else
			combinedPref.setPasswordHash(user.getAccountPreference().getPasswordHash());

		if (user.getAccountPreference() != null) {
			combinedPref.setAddress(user.getAccountPreference().getAddress());
			combinedPref.setCurrentGeoLocation(user.getAccountPreference().getCurrentGeoLocation());
			combinedPref.setPreferredLocations(user.getAccountPreference().getPreferredLocations());
		}

		Update update = new Update();
		update.set("accountPreference", combinedPref);

		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(user.getUsername())), update, User.class);

		if (wr.getLastError().getInt("n") == 1) {
			// Update preference
			user.setAccountPreference(combinedPref);
			return true;
		} else
			return false;
	}

	/**
	 * Update the user's address details
	 * 
	 * @param User
	 * @param Address
	 * @return boolean
	 */
	@Override
	public boolean updateAddress(User user, Address address) {
		if (address == null || user == null)
			throw new IllegalArgumentException("Null Values");

		Address verifiedAddress = new Address();
		boolean addressAlreadySet = true;

		if (user.getAccountPreference() == null)
			addressAlreadySet = false;
		else if (user.getAccountPreference().getAddress() == null)
			addressAlreadySet = false;

		if (address.getStreetName() != null)
			verifiedAddress.setStreetName(address.getStreetName());
		else if (addressAlreadySet)
			verifiedAddress.setStreetName(user.getAccountPreference().getAddress().getStreetName());
		else
			verifiedAddress.setStreetName(null);

		if (address.getStreetNumber() != null)
			verifiedAddress.setStreetNumber(address.getStreetNumber());
		else if (addressAlreadySet)
			verifiedAddress.setStreetNumber(user.getAccountPreference().getAddress().getStreetNumber());
		else
			verifiedAddress.setStreetNumber(null);

		if (address.getAreaCode() != null)
			verifiedAddress.setAreaCode(address.getAreaCode());
		else if (addressAlreadySet)
			verifiedAddress.setAreaCode(user.getAccountPreference().getAddress().getAreaCode());
		else
			verifiedAddress.setAreaCode(null);

		if (address.getCountry() != null)
			verifiedAddress.setCountry(address.getCountry());
		else if (addressAlreadySet)
			verifiedAddress.setCountry(user.getAccountPreference().getAddress().getCountry());
		else
			verifiedAddress.setCountry(null);

		if (address.getCounty() != null)
			verifiedAddress.setCounty(address.getCounty());
		else if (addressAlreadySet)
			verifiedAddress.setCounty(user.getAccountPreference().getAddress().getCounty());
		else
			verifiedAddress.setCounty(null);

		if (address.getCity() != null)
			verifiedAddress.setCity(address.getCity());
		else if (addressAlreadySet)
			verifiedAddress.setCity(user.getAccountPreference().getAddress().getCity());
		else
			verifiedAddress.setCity(null);

		verifiedAddress.setGeoLocation(address.getGeoLocation());

		AccountPreference updatedPreference;

		if (user.getAccountPreference() != null) {
			updatedPreference = new AccountPreference(user.getAccountPreference().getPasswordHash(), user.getAccountPreference().getFirstName(), user
					.getAccountPreference().getLastName(), user.getAccountPreference().getDob(), verifiedAddress, user.getAccountPreference()
					.getCurrentGeoLocation(), user.getAccountPreference().getPreferredLocations());
		} else {
			updatedPreference = new AccountPreference(null, null, null, null, verifiedAddress, null, null);
		}

		Update update = new Update();
		update.set("accountPreference", updatedPreference);

		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(user.getUsername())), update, User.class);

		if (wr.getLastError().getInt("n") == 1) {
			// Update the object
			user.setAccountPreference(updatedPreference);
			return true;
		} else
			return false;
	}

	@Override
	public boolean updateCurrentGeoLocation(User user, String location) {
		if (location == null || user == null)
			throw new IllegalArgumentException("Null Values");

		AccountPreference updatedPreference;
		if (user.getAccountPreference() != null) {
			updatedPreference = new AccountPreference(user.getAccountPreference().getPasswordHash(), user.getAccountPreference().getFirstName(), user
					.getAccountPreference().getLastName(), user.getAccountPreference().getDob(), user.getAccountPreference().getAddress(), location,
					user.getAccountPreference().getPreferredLocations());
		} else {
			updatedPreference = new AccountPreference(null, null, null, null, null, location, null);
		}

		Update update = new Update();
		update.set("accountPreference", updatedPreference);

		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(user.getUsername())), update, User.class);

		if (wr.getLastError().getInt("n") == 1) {
			user.setAccountPreference(updatedPreference);
			return true;
		} else
			return false;
	}

	@Override
	public boolean updatePreferredLocations(User user, List<Address> addressList) {
		if (addressList == null || user == null)
			throw new IllegalArgumentException("Null Values");

		AccountPreference updatedPreference = new AccountPreference(user.getAccountPreference().getPasswordHash(), user.getAccountPreference()
				.getFirstName(), user.getAccountPreference().getLastName(), user.getAccountPreference().getDob(), user.getAccountPreference()
				.getAddress(), user.getAccountPreference().getCurrentGeoLocation(), addressList);

		Update update = new Update();
		update.set("accountPreference", updatedPreference);

		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(user.getUsername())), update, User.class);

		if (wr.getLastError().getInt("n") == 1) {
			user.setAccountPreference(updatedPreference);
			return true;
		} else
			return false;
	}

	/*
	 * Rank handling
	 */

	@Override
	public boolean addRank(User user, Rank rank) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRank(User user, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @param from
	 *            - Object
	 * @param to
	 *            - String (Username)
	 * @return boolean
	 * @throws FriendRequestExistsException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean sendFriendRequest(User from, String toUsername) throws FriendRequestExistsException, FatalDatabaseErrorException {

		// We need to retrieve first to ensure that the Friend request doesn't
		// already exist
		User recipUser = operations.findOne(new Query(Criteria.where("username").is(toUsername)), User.class);

		if (recipUser == null)
			return false;

		FriendRequest request = new FriendRequest(from.getUsername(), recipUser.getUsername(), FriendRequestStatus.PENDING);

		// Check if friend request already exists
		if (recipUser.getFriendRequests() != null) {
			for (FriendRequest next : recipUser.getFriendRequests()) {
				if (next.getSenderUsername().compareToIgnoreCase(request.getSenderUsername()) == 0
						&& next.getReceiverUsername().compareToIgnoreCase(request.getReceiverUsername()) == 0) {
					throw new FriendRequestExistsException(next.getStatus());
				}
			}
		}

		List<FriendRequest> fromFriends = from.getFriendRequests();
		List<FriendRequest> toFriends = recipUser.getFriendRequests();

		if(fromFriends == null)
			fromFriends = new ArrayList<FriendRequest>();
		if(toFriends == null)
			toFriends = new ArrayList<FriendRequest>();
		
		fromFriends.add(request);
		toFriends.add(request);

		Update updateFrom = new Update();
		updateFrom.set("friendRequests", fromFriends);

		Update updateTo = new Update();
		updateTo.set("friendRequests", toFriends);

		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(from.getUsername())), updateFrom, User.class);

		if (wr.getLastError().getInt("n") == 1) {
			wr = operations.updateFirst(new Query(Criteria.where("username").is(toUsername)), updateTo, User.class);

			if (wr.getLastError().getInt("n") == 1)
				return true;
			else {
				log.error("Failed to update friend request. This is a FATAL error. For user: " + recipUser + ". With Friends List" + toFriends);
				throw new FatalDatabaseErrorException("Failed to update friends list of " + recipUser);
			}
		} else { // fatal
			log.error("Failed to update friend request. This is a FATAL error. For user: " + from + ". With Friends List" + fromFriends);
			throw new FatalDatabaseErrorException("Failed to update friends list of " + from);
		}
	}

	/**
	 * Accept the friend request that has been RECEIVED
	 * 
	 * @param caller
	 * @param friendsUsername
	 * @return boolean
	 * @throws FriendRequestDoesNotExistException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean acceptFriendRequest(User caller, String friendUsername) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation {
		if(caller == null || friendUsername == null) {
			log.debug("Null Parameters");
			return false;
		}
		
		if(caller.getFriendRequests() == null)
			throw new UserObjectNotInitializedForOperation("Friend Request list is null");
		
		List<FriendRequest> requests = caller.getFriendRequests();
		boolean updated = false;
		for(int i=0; i < requests.size(); i++) {
			
			if(requests.get(i).getSenderUsername().compareToIgnoreCase(friendUsername) == 0) {
				if(requests.get(i).getStatus() == FriendRequestStatus.ACCEPTED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.ACCEPTED);
				requests.get(i).setStatus(FriendRequestStatus.ACCEPTED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User "  + caller.getUsername() + " has no friend request from: " + friendUsername);
		
		User friend = operations.findOne(new Query(Criteria.where("username").is(friendUsername)), User.class);
		
		if(friend == null)
			throw new FatalDatabaseErrorException("Failed to get other user's User information");
		
		List<FriendRequest> otherRequests = friend.getFriendRequests();

		updated = false;
		for(int i=0; i < otherRequests.size(); i++) {
			
			if(otherRequests.get(i).getReceiverUsername().compareToIgnoreCase(caller.getUsername()) == 0) {
				if(otherRequests.get(i).getStatus() == FriendRequestStatus.ACCEPTED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.ACCEPTED);
				otherRequests.get(i).setStatus(FriendRequestStatus.ACCEPTED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User " + friendUsername + " has no friend request from: " + caller.getUsername());
		
		Update update = new Update();
		update.set("friendRequests", requests);
		
		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(caller.getUsername())), update, User.class);
		
		if(wr.getLastError().getInt("n") == 1) {
			update = new Update();
			update.set("friendRequests", otherRequests);
			wr = operations.updateFirst(new Query(Criteria.where("username").is(friendUsername)), update, User.class);
			
			if(wr.getLastError().getInt("n") == 1 )
				return true;
			else
				throw new FatalDatabaseErrorException("Unable to update other Friend Request to ACCEPTED");
		}
		else
			throw new FatalDatabaseErrorException("Unable to update Friend Request to ACCEPTED");		
	}

	/**
	 * Deny a friend request that has been received by the caller
	 * 
	 * @param caller
	 * @param friendsUsername
	 * @return boolean
	 * @throws FriendRequestDoesNotExistException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean denyFriendRequest(User caller, String friendUsername) throws FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation{
		if(caller == null || friendUsername == null) {
			log.debug("Null Parameters");
			throw new IllegalArgumentException("Null Parameters");
		}
		
		if(caller.getFriendRequests() == null)
			throw new UserObjectNotInitializedForOperation("Friend Request list is null");
		
		
		List<FriendRequest> requests = caller.getFriendRequests();
		boolean updated = false;
		for(int i=0; i < requests.size(); i++) {
			
			if(requests.get(i).getSenderUsername().compareToIgnoreCase(friendUsername) == 0) {
				requests.remove(i);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User "  + caller.getUsername() + " has no friend request from: " + friendUsername);
		
		User friend = operations.findOne(new Query(Criteria.where("username").is(friendUsername)), User.class);
		
		if(friend == null)
			throw new FatalDatabaseErrorException("Failed to get other user's User information");
		
		List<FriendRequest> otherRequests = friend.getFriendRequests();

		updated = false;
		for(int i=0; i < otherRequests.size(); i++) {
			
			if(otherRequests.get(i).getReceiverUsername().compareToIgnoreCase(caller.getUsername()) == 0) {
				otherRequests.remove(i);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User " + friendUsername + " has no friend request from: " + caller.getUsername());
		
		Update update = new Update();
		update.set("friendRequests", requests);
		
		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(caller.getUsername())), update, User.class);
		
		if(wr.getLastError().getInt("n") == 1) {
			update = new Update();
			update.set("friendRequests", otherRequests);
			wr = operations.updateFirst(new Query(Criteria.where("username").is(friendUsername)), update, User.class);
			
			if(wr.getLastError().getInt("n") == 1 )
				return true;
			else
				throw new FatalDatabaseErrorException("Unable to update other Friend Request to DENIED");
		}
		else
			throw new FatalDatabaseErrorException("Unable to update Friend Request to DENIED");
	}
	
	/**
	 * Block a friend request that has been received by the caller
	 * 
	 * @param caller
	 * @param friendsUsername
	 * @return boolean
	 * @throws FriendRequestDoesNotExistException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean blockFriendRequest(User caller, String friendUsername) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation {
		if(caller == null || friendUsername == null) {
			log.debug("Null Parameters");
			return false;
		}
		
		if(caller.getFriendRequests() == null)
			throw new UserObjectNotInitializedForOperation("Friend Request list is null");
		
		
		List<FriendRequest> requests = caller.getFriendRequests();
		boolean updated = false;
		for(int i=0; i < requests.size(); i++) {
			
			if(requests.get(i).getSenderUsername().compareToIgnoreCase(friendUsername) == 0) {
				if(requests.get(i).getStatus() == FriendRequestStatus.BLOCKED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.BLOCKED);
				requests.get(i).setStatus(FriendRequestStatus.BLOCKED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User "  + caller.getUsername() + " has no friend request from: " + friendUsername);
		
		User friend = operations.findOne(new Query(Criteria.where("username").is(friendUsername)), User.class);
		
		if(friend == null)
			throw new FatalDatabaseErrorException("Failed to get other user's User information");
		
		List<FriendRequest> otherRequests = friend.getFriendRequests();

		updated = false;
		for(int i=0; i < otherRequests.size(); i++) {
			
			if(otherRequests.get(i).getReceiverUsername().compareToIgnoreCase(caller.getUsername()) == 0) {
				if(otherRequests.get(i).getStatus() == FriendRequestStatus.BLOCKED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.BLOCKED);
				otherRequests.get(i).setStatus(FriendRequestStatus.BLOCKED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User " + friendUsername + " has no friend request from: " + caller.getUsername());
		
		Update update = new Update();
		update.set("friendRequests", requests);
		
		WriteResult wr = operations.updateFirst(new Query(Criteria.where("username").is(caller.getUsername())), update, User.class);
		
		if(wr.getLastError().getInt("n") == 1) {
			update = new Update();
			update.set("friendRequests", otherRequests);
			wr = operations.updateFirst(new Query(Criteria.where("username").is(friendUsername)), update, User.class);
			
			if(wr.getLastError().getInt("n") == 1 )
				return true;
			else
				throw new FatalDatabaseErrorException("Unable to update other Friend Request to BLOCKED");
		}
		else
			throw new FatalDatabaseErrorException("Unable to update Friend Request to BLOCKED");
	}
	
	
}
