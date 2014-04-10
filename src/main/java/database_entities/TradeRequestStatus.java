/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines an enum associated with the current status associated to 
 * a Trade Request
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

public enum TradeRequestStatus {
	ACCEPTED(0), 
	PENDING(1), 
	DECLINED(2);
	
	private int value;
	
	private TradeRequestStatus( int val ) {
		this.value = val;
	}
	
	public int getStatus() {
		return this.value;
	}
}
