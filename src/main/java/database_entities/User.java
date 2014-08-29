/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the the information regarding a User's details
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Document
public class User extends DatabaseDocument{

	@Indexed(unique = true)
	private String username;
	@Indexed(unique = true)
	private String email;
	
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date dateJoined;
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date lastLogin;
	
	private String facebookId;
	
	private String city;
	private double [] position;
	
	private String profilePictureId;	//Needs implementing
	
	private AccountPreference accountPreference;
	private TradeRoomMeta tradeRoomMeta;
	private List<Rank> ranks;
	private List<FriendRequest> friendRequests;	//Eventually, we are going to want to convert these to IDs as well. (What a fun process that will be)

	private List<String> tradeRequests;	//Ids
	
	private UserRole role;	//Needs implementing

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
		this.position = new double[2];
	}
	
	/**
	 * For GEO LOCATION testing ONLY. Not for use otherwise
	 */
	public User(String city, double longitude, double latitude) {
		this.city = city;
		this.position = new double[2];
		this.position[0] = longitude;
		this.position[1] = latitude;
	}

	/**
	 * The main constructor for populatin a User object
	 * 
	 * @param username
	 * @param pref
	 * @param ranks
	 * @param friendsList
	 * @param friendRequests
	 * @param tradeRequests
	 */
	public User(String username, String email, AccountPreference pref, TradeRoomMeta tradeRoomMeta, List<Rank> ranks,
			List<FriendRequest> friendRequests, List<String> tradeRequests, Date dateJoined) {
		this.position = new double[2];
		this.username = username;
		this.email = email;
		this.accountPreference = pref;
		this.ranks = ranks;
		this.tradeRoomMeta = tradeRoomMeta;
		this.friendRequests = friendRequests;
		this.tradeRequests = tradeRequests;
		this.dateJoined = dateJoined;
	}

	/*
	 * Getters / Setters
	 */
	/*public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	} */
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}
	
	public String getProfilePictureId() {
		return profilePictureId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String profilePictureId() {
		return this.profilePictureId();
	}
	
	public void setProfilePictureId(String profilePictureId) {
		this.profilePictureId = profilePictureId;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AccountPreference getAccountPreference() {
		return accountPreference;
	}

	public void setAccountPreference(AccountPreference accountPreference) {
		this.accountPreference = accountPreference;
	}

	public List<Rank> getRanks() {
		return ranks;
	}

	public void setRanks(List<Rank> ranks) {
		this.ranks = ranks;
	}

	public List<String> getTradeRequests() {
		return tradeRequests;
	}

	public void setTradeRequests(List<String> tradeRequests) {
		this.tradeRequests = tradeRequests;
	}

	public List<FriendRequest> getFriendRequests() {
		return friendRequests;
	}

	public void setFriendRequests(List<FriendRequest> friendRequests) {
		this.friendRequests = friendRequests;
	}
	
	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public TradeRoomMeta getTradeRoomMeta() {
		return tradeRoomMeta;
	}

	public void setTradeRoomMeta(TradeRoomMeta tradeRoomMeta) {
		this.tradeRoomMeta = tradeRoomMeta;
	}
	
	public UserRole getRole() {
		return this.role;
	}
	
	public void setRole(UserRole userRole) {
		this.role = userRole;
	}
	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((accountPreference == null) ? 0 : accountPreference
						.hashCode());
		result = prime * result
				+ ((dateJoined == null) ? 0 : dateJoined.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((friendRequests == null) ? 0 : friendRequests.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ranks == null) ? 0 : ranks.hashCode());
		result = prime * result
				+ ((tradeRequests == null) ? 0 : tradeRequests.hashCode());
		result = prime * result
				+ ((tradeRoomMeta == null) ? 0 : tradeRoomMeta.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

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
		if (dateJoined == null) {
			if (other.dateJoined != null)
				return false;
		} else if (!dateJoined.equals(other.dateJoined))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (friendRequests == null) {
			if (other.friendRequests != null)
				return false;
		} else if (!friendRequests.equals(other.friendRequests))
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
		if (tradeRoomMeta == null) {
			if (other.tradeRoomMeta != null)
				return false;
		} else if (!tradeRoomMeta.equals(other.tradeRoomMeta))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email
				+ ", dateJoined=" + dateJoined + ", lastLogin=" + lastLogin
				+ ", accountPreference=" + accountPreference
				+ ", tradeRoomMeta=" + tradeRoomMeta + ", ranks=" + ranks
				+ ", friendRequests=" + friendRequests + ", tradeRequests="
				+ tradeRequests + "]";
	}
	
	public enum UserRole {
		USER(0),
		ADMIN(1),
		ROOT(10);
		
		private int value;
		private UserRole(int val) {
			this.value = val;
		}
		
		public int getValue() {
			return this.value;
		}
	}
}
