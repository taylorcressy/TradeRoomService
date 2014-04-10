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

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class User {

	@Id
	private String id;
	
	@Indexed(unique=true)
	private String username;
	private AccountPreference accountPreference;
	private List<Integer> ranks;
	private List<String> friendsList;
	private List<TradeRequest> tradeRequests;

	// No need to reference the Trade Room ID, will be held on it's own

	public User(String username, AccountPreference pref, List<Integer> ranks, List<String> friendsList, List<TradeRequest> tradeRequests) {
		this.username = username;
		this.accountPreference = pref;
		this.ranks = ranks;
		this.friendsList = friendsList;
		this.tradeRequests = tradeRequests;
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
