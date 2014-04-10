/**
 * Entity interface for use with MongoDB Data / Spring
 * Enum defining a Trade Room Item's condition
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

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
}
