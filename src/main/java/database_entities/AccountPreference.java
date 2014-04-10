/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the meta account preferences associated with a
 * user.
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

import java.sql.Timestamp;
import java.util.List;

public class AccountPreference {
	transient private String passwordHash; // Stop from sending over wire
	private String firstName;
	private String lastName;
	private String dob; // Date of birth
	private Address address;
	private String defaultGeoLocation;
	private List<Address> preferredLocations;

	/*
	 * Constructor
	 */
	public AccountPreference(String passwordHash, String firstName, String lastName, String dob, Address address, String defaultGeoLocation,
			List<Address> preferredLocations) {
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.address = address;
		this.defaultGeoLocation = defaultGeoLocation;
		this.preferredLocations = preferredLocations;
	}

	/*
	 * Getters / Setters
	 */
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getDefaultLocation() {
		return defaultGeoLocation;
	}

	public void setDefaultLocation(String defaultLocation) {
		this.defaultGeoLocation = defaultLocation;
	}

	public List<Address> getPreferredLocations() {
		return preferredLocations;
	}

	public void setPreferredLocations(List<Address> preferredLocations) {
		this.preferredLocations = preferredLocations;
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
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((defaultGeoLocation == null) ? 0 : defaultGeoLocation.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((preferredLocations == null) ? 0 : preferredLocations.hashCode());
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
		AccountPreference other = (AccountPreference) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (defaultGeoLocation == null) {
			if (other.defaultGeoLocation != null)
				return false;
		} else if (!defaultGeoLocation.equals(other.defaultGeoLocation))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (preferredLocations == null) {
			if (other.preferredLocations != null)
				return false;
		} else if (!preferredLocations.equals(other.preferredLocations))
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
		return "AccountPreference [firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", address=" + address
				+ ", defaultGeoLocation=" + defaultGeoLocation + ", preferredLocations=" + preferredLocations + "]";
	}

}
