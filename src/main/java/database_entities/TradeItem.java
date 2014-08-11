/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the details associated with a Trade Room Item
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TradeItem {
	/**
	 * Inner class - specifies the item condition of a trade item
	 */
	public enum ItemCondition {
		NEW(0), 
		LIKE_NEW(1), 
		REFURBISHED(2), 
		USED(3), 
		GOOD(4), 
		FAIR(5), 
		ACCEPTABLE(6), 
		POOR(7);
		
		private int value;
		
		private ItemCondition(int val) {
			this.value = val;
		}
		
		public int getValue() {
			return this.value;
		}
		
		public static String[] names() {
		    ItemCondition[] conditions = values();
		    String[] names = new String[conditions.length];

		    for (int i = 0; i < conditions.length; i++) {
		        names[i] = conditions[i].name();
		    }

		    return names;
		}
	}
	

	@Id private String id;
	
	@TextIndexed(weight=3) private String name;
	@TextIndexed private String description;
	@TextIndexed(weight=2) private List<String> tags;
	
	private List<String> imageIds;
	private int count;
	private ItemCondition condition;
	private String dateAdded;
	private String ownerId;

	public TradeItem(String name, String description, List<String> tags, List<String> imageIds, int count, ItemCondition condition, String dateAdded,
			String ownerId) {
		this.name = name;
		this.description = description;
		this.condition = condition;
		this.tags = tags;
		this.imageIds = imageIds;
		this.count = count;
		this.dateAdded = dateAdded;
		this.ownerId = ownerId;
	}

	
	/*
	 * Database Operations
	 */
	/**
	 * Create a new Trade Item 
	 * @return boolean
	 */
	public boolean createNewItem() {
		return true;
	}
	
	/**
	 * Read a trade item from the database
	 * @return boolean
	 */
	public boolean readItem() {
		return true;
	}
	
	/**
	 * Update a trade item in the databse
	 * @return boolean
	 */
	public boolean updateItem() {
		return true;
	}
	
	/**
	 * Delete a trade item from the database
	 * @return
	 */
	public boolean deleteItem() {
		return true;
	}
	
	/*
	 * Getters/Setters
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public List<String> getImageIds() {
		return this.imageIds;
	}
	
	public void setImageIds(List<String> imageIds) {
		this.imageIds = imageIds;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ItemCondition getCondition() {
		return condition;
	}

	public void setCondition(ItemCondition condition) {
		this.condition = condition;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
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
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + count;
		result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
		TradeItem other = (TradeItem) obj;
		if (condition != other.condition)
			return false;
		if (count != other.count)
			return false;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
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
		return "TradeItem [id=" + id + ", name=" + name + ", description=" + description + ", tags=" + tags
				+ ", count=" + count + ", condition=" + condition + ", dateAdded=" + dateAdded + ", ownerId=" + ownerId + "]";
	}
}
