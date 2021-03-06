package database_entities.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.spockframework.gentyref.TypeToken;
import org.springframework.dao.DuplicateKeyException;

import com.google.gson.Gson;

public class DetailedDuplicateKeyException extends DuplicateKeyException {

	/**
	 * Auto generated by eclipse
	 */
	private static final long serialVersionUID = -6495425042987425785L;
	private String message = null;
	private String duplicatedIndex;
	 
    public DetailedDuplicateKeyException(String message) {
    	super(message);
    	
    	/*Discover which index was duplicated*/
    	Map<String, String> map = new HashMap<String, String>();
    	map = new Gson().fromJson(message, new TypeToken<Map<String, String>>(){}.getType());
    	String err = map.get("err");
    	String brokenErr[] = err.split(" ");
    	String brokenIndexErr[];
    	for(String next: brokenErr) {
    		if(next.startsWith("TradeRoom")) {
    			brokenIndexErr = next.split("\\.");
    			this.duplicatedIndex = brokenIndexErr[brokenIndexErr.length - 1];
    		    this.duplicatedIndex = this.duplicatedIndex.substring(1);
    			break;
    		}
    	}
    	
    	this.message = message;
    }
 
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
    
    public String getDuplicatedIndex() {
    	return this.duplicatedIndex;
    }
}
