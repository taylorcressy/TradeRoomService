/**
 * High level document object. For all entities that get stored at in Mongo
 * 
 * Note: All objects that extend from this must declare the '@Document' on their own.
 */
package database_entities;

import org.springframework.data.annotation.Id;

public class DatabaseDocument {

	@Id
	protected String id;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
}
