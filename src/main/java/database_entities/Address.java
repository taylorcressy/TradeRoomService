/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the meta Address associated with an Account Preference.
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

public class Address {
	private String streetName;
	private String streetNumber;
	private String areaCode;
	private String country;
	private String city;
	private String county;
	private String geoLocation;
	
	
	/*
	 * Empty constructor
	 */
	public Address() {
		//No implementation
	}
	
	/*
	 * Constructor
	 */
	public Address(String streetName, String streetNumber, String areaCode, String country, String city, String county, String geoLocation) {
		this.streetName = streetName;
		this.streetNumber = streetNumber;
		this.areaCode = areaCode;
		this.country = country;
		this.county = county;
		this.city = city;
		this.geoLocation = geoLocation;
	}
	
	/*
	 * Getters and Setters
	 */
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getGeoLocation() {
		return this.geoLocation;
	}
	public void setGeoLocation(String location) {
		this.geoLocation = location;
	}
	
	
	/**
	 * hashCode method
	 * 
	 * @return int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((areaCode == null) ? 0 : areaCode.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((county == null) ? 0 : county.hashCode());
		result = prime * result + ((streetName == null) ? 0 : streetName.hashCode());
		result = prime * result + ((streetNumber == null) ? 0 : streetNumber.hashCode());
		result = prime * result + ((geoLocation == null) ? 0 : geoLocation.hashCode());
		return result;
	}
	
	/**
	 * equals method
	 * 
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (areaCode == null) {
			if (other.areaCode != null)
				return false;
		} else if (!areaCode.equals(other.areaCode))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (county == null) {
			if (other.county != null)
				return false;
		} else if (!county.equals(other.county))
			return false;
		if (streetName == null) {
			if (other.streetName != null)
				return false;
		} else if (!streetName.equals(other.streetName))
			return false;
		if (streetNumber == null) {
			if (other.streetNumber != null)
				return false;
		} else if (!streetNumber.equals(other.streetNumber))
			return false;
		if (geoLocation == null) {
			if (other.geoLocation != null)
				return false;
		} else if (!geoLocation.equals(other.geoLocation))
			return false;
		return true;
	}
	
	/**
	 * toString method
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		return "Address [streetName=" + streetName + ", streetNumber=" + streetNumber + ", areaCode=" + areaCode + ", country=" + country + ", city="
				+ city + ", county=" + county + ", geoLocation=" + geoLocation + "]";
	}
}
