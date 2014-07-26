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

import org.bson.types.ObjectId;
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
	
	/**
	 *
	 */
	/**
	 * 
	 * @param from
	 *            - Object
	 * @param to
	 *            - String (Id)
	 * @return boolean
	 * @throws FriendRequestExistsException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean sendFriendRequest(User from, String toId) throws FriendRequestExistsException, FatalDatabaseErrorException {

		// We need to retrieve first to ensure that the Friend request doesn't
		// already exist
		User recipUser = operations.findOne(new Query(Criteria.where("_id").is(new ObjectId(toId))), User.class);

		if (recipUser == null)
			return false;

		FriendRequest request = new FriendRequest(from.getId(), recipUser.getId(), FriendRequestStatus.PENDING);

		// Check if friend request already exists
		if (recipUser.getFriendRequests() != null) {
			for (FriendRequest next : recipUser.getFriendRequests()) {
				if (next.getSenderId().compareToIgnoreCase(request.getSenderId()) == 0
						&& next.getReceiverId().compareToIgnoreCase(request.getReceiverId()) == 0) {
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

		WriteResult wr = operations.updateFirst(new Query(Criteria.where("_id").is(new ObjectId(from.getId()))), updateFrom, User.class);

		if (wr.getLastError().getInt("n") == 1) {
			wr = operations.updateFirst(new Query(Criteria.where("_id").is(new ObjectId(toId))), updateTo, User.class);

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
	 * @param friendsId
	 * @return boolean
	 * @throws FriendRequestDoesNotExistException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean acceptFriendRequest(User caller, String friendId) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation {
		if(caller == null || friendId == null) {
			log.debug("Null Parameters");
			return false;
		}
		
		if(caller.getFriendRequests() == null)
			throw new UserObjectNotInitializedForOperation("Friend Request list is null");
		
		List<FriendRequest> requests = caller.getFriendRequests();
		boolean updated = false;
		for(int i=0; i < requests.size(); i++) {
			
			if(requests.get(i).getSenderId().compareToIgnoreCase(friendId) == 0) {
				if(requests.get(i).getStatus() == FriendRequestStatus.ACCEPTED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.ACCEPTED);
				requests.get(i).setStatus(FriendRequestStatus.ACCEPTED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User "  + caller.getId() + " has no friend request from: " + friendId);
		
		User friend = operations.findOne(new Query(Criteria.where("_id").is(new ObjectId(friendId))), User.class);
		
		if(friend == null)
			throw new FatalDatabaseErrorException("Failed to get other user's User information");
		
		List<FriendRequest> otherRequests = friend.getFriendRequests();

		updated = false;
		for(int i=0; i < otherRequests.size(); i++) {
			
			if(otherRequests.get(i).getReceiverId().compareToIgnoreCase(caller.getId()) == 0) {
				if(otherRequests.get(i).getStatus() == FriendRequestStatus.ACCEPTED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.ACCEPTED);
				otherRequests.get(i).setStatus(FriendRequestStatus.ACCEPTED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User " + friendId + " has no friend request from: " + caller.getId());
		
		Update update = new Update();
		update.set("friendRequests", requests);
		
		WriteResult wr = operations.updateFirst(new Query(Criteria.where("_id").is(new ObjectId(caller.getId()))), update, User.class);
		
		if(wr.getLastError().getInt("n") == 1) {
			update = new Update();
			update.set("friendRequests", otherRequests);
			wr = operations.updateFirst(new Query(Criteria.where("_id").is(new ObjectId(friendId))), update, User.class);
			
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
	 * @param friendsId
	 * @return boolean
	 * @throws FriendRequestDoesNotExistException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean denyFriendRequest(User caller, String friendId) throws FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation{
		if(caller == null || friendId == null) {
			log.debug("Null Parameters");
			throw new IllegalArgumentException("Null Parameters");
		}
		
		if(caller.getFriendRequests() == null)
			throw new UserObjectNotInitializedForOperation("Friend Request list is null");
		
		
		List<FriendRequest> requests = caller.getFriendRequests();
		boolean updated = false;
		for(int i=0; i < requests.size(); i++) {
			
			if(requests.get(i).getSenderId().compareToIgnoreCase(friendId) == 0) {
				requests.remove(i);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User "  + caller.getId() + " has no friend request from: " + friendId);
		
		User friend = operations.findOne(new Query(Criteria.where("_id").is(new ObjectId(friendId))), User.class);
		
		if(friend == null)
			throw new FatalDatabaseErrorException("Failed to get other user's User information");
		
		List<FriendRequest> otherRequests = friend.getFriendRequests();

		updated = false;
		for(int i=0; i < otherRequests.size(); i++) {
			
			if(otherRequests.get(i).getReceiverId().compareToIgnoreCase(caller.getId()) == 0) {
				otherRequests.remove(i);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User " + friendId + " has no friend request from: " + caller.getId());
		
		Update update = new Update();
		update.set("friendRequests", requests);
		
		WriteResult wr = operations.updateFirst(new Query(Criteria.where("_id").is(new ObjectId(caller.getId()))), update, User.class);
		
		if(wr.getLastError().getInt("n") == 1) {
			update = new Update();
			update.set("friendRequests", otherRequests);
			wr = operations.updateFirst(new Query(Criteria.where("_id").is(friendId)), update, User.class);
			
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
	 * @param friendsId
	 * @return boolean
	 * @throws FriendRequestDoesNotExistException
	 * @throws FatalDatabaseErrorException
	 */
	@Override
	public boolean blockFriendRequest(User caller, String friendId) throws FriendRequestExistsException, FriendRequestDoesNotExistException, FatalDatabaseErrorException, UserObjectNotInitializedForOperation {
		if(caller == null || friendId == null) {
			log.debug("Null Parameters");
			return false;
		}
		
		if(caller.getFriendRequests() == null)
			throw new UserObjectNotInitializedForOperation("Friend Request list is null");
		
		
		List<FriendRequest> requests = caller.getFriendRequests();
		boolean updated = false;
		for(int i=0; i < requests.size(); i++) {
			
			if(requests.get(i).getSenderId().compareToIgnoreCase(friendId) == 0) {
				if(requests.get(i).getStatus() == FriendRequestStatus.BLOCKED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.BLOCKED);
				requests.get(i).setStatus(FriendRequestStatus.BLOCKED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User "  + caller.getId() + " has no friend request from: " + friendId);
		
		User friend = operations.findOne(new Query(Criteria.where("_id").is(new ObjectId(friendId))), User.class);
		
		if(friend == null)
			throw new FatalDatabaseErrorException("Failed to get other user's User information");
		
		List<FriendRequest> otherRequests = friend.getFriendRequests();

		updated = false;
		for(int i=0; i < otherRequests.size(); i++) {
			
			if(otherRequests.get(i).getReceiverId().compareToIgnoreCase(caller.getId()) == 0) {
				if(otherRequests.get(i).getStatus() == FriendRequestStatus.BLOCKED)
					throw new FriendRequestExistsException("Friend request already exists", FriendRequestStatus.BLOCKED);
				otherRequests.get(i).setStatus(FriendRequestStatus.BLOCKED);
				updated = true;
			}
		}
		
		if(updated == false)
			throw new FriendRequestDoesNotExistException("User " + friendId + " has no friend request from: " + caller.getId());
		
		Update update = new Update();
		update.set("friendRequests", requests);
		
		WriteResult wr = operations.updateFirst(new Query(Criteria.where("_id").is(caller.getId())), update, User.class);
		
		if(wr.getLastError().getInt("n") == 1) {
			update = new Update();
			update.set("friendRequests", otherRequests);
			wr = operations.updateFirst(new Query(Criteria.where("_id").is(new ObjectId(friendId))), update, User.class);
			
			if(wr.getLastError().getInt("n") == 1 )
				return true;
			else
				throw new FatalDatabaseErrorException("Unable to update other Friend Request to BLOCKED");
		}
		else
			throw new FatalDatabaseErrorException("Unable to update Friend Request to BLOCKED");
	}
	
	
}
