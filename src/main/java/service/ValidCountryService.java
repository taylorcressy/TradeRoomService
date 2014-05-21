package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import server_utilities.CSVReader;
import database_entities.exceptions.LoadDBWithCSVFailed;

@Service
public class ValidCountryService {
	
	transient private static Logger log = LoggerFactory.getLogger("service-logger");
		
	Map<String, ValidCountry> validCountries; /*<Country Name, Country Object>*/
	
	public ValidCountryService() {
		log.debug("Populating list of valid Countries");
		validCountries = new HashMap<String, ValidCountryService.ValidCountry>();
		populateValidCountries();
	}
	
	
	public Collection<ValidCountry> retrieveListOfValidCountries() {
		return validCountries.values();
	}
	
	private void populateValidCountries() {
		try {
			CSVReader csv = new CSVReader("countries");
			ArrayList<String[]> arr = csv.getCSVArray();
			
			for(String[] next: arr) {
				if(next.length != 0) {
					next[1] = next[1].trim().replace("\"", "");
					validCountries.put(next[1], new ValidCountry(next[0], next[1]));
				}
			}
		}
		catch(IOException ie) {
			log.error("Failed to load the server errors resource file: " + ie.getLocalizedMessage());
			throw new LoadDBWithCSVFailed("IO Error");
		}
	}
	
	/**
	 * Returns the country object associated with the country  name
	 * 
	 * If it is not a valid country, null will be returned
	 * @param CountryName
	 * @return ValidCountry
	 */
	public ValidCountry getValidCountry(String countryName) {
		return validCountries.get(countryName);
	}
	
	
	public class ValidCountry {
		
		private String countryCode;	
		private String countryName;
		
		public ValidCountry(String countryCode, String countryName) {
			this.countryCode = countryCode;
			this.countryName = countryName;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

		public String getCountryName() {
			return countryName;
		}

		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}

		@Override
		public String toString() {
			return "ValidCountry [countryCode=" + countryCode + ", countryName=" + countryName + "]";
		}
	}
	
}
