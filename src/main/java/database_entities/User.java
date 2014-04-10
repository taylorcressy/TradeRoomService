/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the the information regarding a User's details
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

import java.util.List;

import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.CommandResult;
import com.mongodb.WriteResult;

@Document
public class User {

	@Transient
	transient private static Logger log = LoggerFactory.getLogger("database-logger");

	@Id
	private String id;

	@Indexed(unique = true)
	private String username;
	private AccountPreference accountPreference;
	private List<Integer> ranks;
	private List<String> friendsList;
	private List<TradeRequest> tradeRequests;

	public User() {
		// Empty constructor
	}

	/**
	 * Convenience constructor to just populate the username for future DB calls
	 * 
	 * @param username
	 */
	public User(String username) {
		this.username = username;
	}

	/**
	 * The main constructor for populatin a User object
	 * 
	 * @param username
	 * @param pref
	 * @param ranks
	 * @param friendsList
	 * @param tradeRequests
	 */
	public User(String username, AccountPreference pref, List<Integer> ranks, List<String> friendsList, List<TradeRequest> tradeRequests) {
		this.username = username;
		this.accountPreference = pref;
		this.ranks = ranks;
		this.friendsList = friendsList;
		this.tradeRequests = tradeRequests;
	}

	/*
	 * DB operations
	 */
	/**
	 * Create a new user within the database using this object. If two user's
	 * with the same username are found, this will return false
	 * 
	 * @return boolean
	 */
	public boolean createNewUser() {
		MongoTemplate operations = RepositoryFactory.getMongoOperationsInstance();
		try {
			log.debug("Saving new user to DB");
			operations.save(this);
			return true;
		} catch (DuplicateKeyException dke) {
			log.debug("User with same username found");
			return false;
		}
	}

	/**
	 * Find the user associated with this object's username.
	 * 
	 * @param username
	 * @throws IllegalArgumentException
	 * @return User
	 */
	public boolean readUser() {
		if (this.username == null)
			throw new IllegalArgumentException("Must specify a username");

		MongoTemplate operations = RepositoryFactory.getMongoOperationsInstance();

		Query query = new Query(new Criteria("username").is(username));

		User retUser = operations.findOne(query, User.class);

		if (retUser == null)
			return false;

		this.convertUser(retUser);
		log.debug("Successfully retrieved " + this);
		return true;
	}

	/**
	 * Update the current object into the database. If this object's username is
	 * not set, IllegalArgumentException will be thrown
	 * 
	 * @throws IllegalArgumentException
	 * @return boolean
	 */
	public boolean updateUser() {
		if (this.username == null)
			throw new IllegalArgumentException("The object's username field must be set");

		MongoTemplate operations = RepositoryFactory.getMongoOperationsInstance();

		Query query = new Query(new Criteria("username").is(this.username));
		Update update = new Update();

		update.set("accountPreference", this.accountPreference);
		update.set("friendsList", this.friendsList);
		update.set("ranks", this.ranks);
		update.set("tradeRequests", this.tradeRequests);

		WriteResult wr = operations.updateFirst(query, update, User.class);
		Integer succ = (Integer) wr.getLastError().get("n");

		if (succ == 1) {
			log.debug("Updated User " + this);
			return true;
		} else
			return false;
	}

	/**
	 * Delete the user with the associated username. If username is null, the
	 * member variable username will be used. If this is null as well, an
	 * Illegal argument exception will be thrown
	 * 
	 * @param username
	 * @throws IllegalArgumentException
	 * @return boolean
	 */
	public boolean deleteUser() {
		if (this.username == null)
			throw new IllegalArgumentException("Must specify a username");

		MongoTemplate operations = RepositoryFactory.getMongoOperationsInstance();

		Query query = new Query(new Criteria("username").is(this.username));

		operations.remove(query, User.class);

		CommandResult cr = operations.getDb().getLastError();
		Integer res = (Integer) cr.get("n");

		if (res == 1)
			return true;
		else
			return false;
	}

	/**
	 * Helper to onvert the user object to this User Object
	 * 
	 * @return void
	 */
	private void convertUser(User user) {
		if (user == null)
			return;

		this.id = user.getId();
		this.username = user.getUsername();
		this.accountPreference = user.getAccountPreference();
		this.ranks = user.getRanks();
		this.friendsList = user.getFriendsList();
		this.tradeRequests = user.getTradeRequests();
	}

	/*
	 * Getters / Setters
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AccountPreference getAccountPreference() {
		return accountPreference;
	}

	public void setAccountPreference(AccountPreference accountPreference) {
		this.accountPreference = accountPreference;
	}

	public List<Integer> getRanks() {
		return ranks;
	}

	public void setRanks(List<Integer> ranks) {
		this.ranks = ranks;
	}

	public List<String> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<String> friendsList) {
		this.friendsList = friendsList;
	}

	public List<TradeRequest> getTradeRequests() {
		return tradeRequests;
	}

	public void setTradeRequests(List<TradeRequest> tradeRequests) {
		this.tradeRequests = tradeRequests;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountPreference == null) ? 0 : accountPreference.hashCode());
		result = prime * result + ((friendsList == null) ? 0 : friendsList.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ranks == null) ? 0 : ranks.hashCode());
		result = prime * result + ((tradeRequests == null) ? 0 : tradeRequests.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (accountPreference == null) {
			if (other.accountPreference != null)
				return false;
		} else if (!accountPreference.equals(other.accountPreference))
			return false;
		if (friendsList == null) {
			if (other.friendsList != null)
				return false;
		} else if (!friendsList.equals(other.friendsList))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ranks == null) {
			if (other.ranks != null)
				return false;
		} else if (!ranks.equals(other.ranks))
			return false;
		if (tradeRequests == null) {
			if (other.tradeRequests != null)
				return false;
		} else if (!tradeRequests.equals(other.tradeRequests))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", accountPreference=" + accountPreference + ", ranks=" + ranks + ", friendsList="
				+ friendsList + ", tradeRequests=" + tradeRequests + "]";
	}
}
