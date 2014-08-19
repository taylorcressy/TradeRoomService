/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the information associated with a Trade Request
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_entities;

import java.util.Date;
import java.util.List;

import database_entities.TradeItem.ItemCondition;

public class TradeRequest {

	private String from;				/*ID*/
	private String to;					/*ID*/
	private List<String> fromItems;		/*Ids*/
	private List<String> toItems;		/*Ids*/
	private Date dateInitiated;
	private TradeRequestStatus status;
	private TradeMethod methodFrom;
	private TradeMethod methodTo;
	private String message;
	private boolean counterRequest;

	/*
	 * Constructor
	 */
	public TradeRequest(String from, String to, List<String> fromItems, List<String> toItems,
			Date dateInitiated, TradeRequestStatus status, TradeMethod methodTo, TradeMethod methodFrom, boolean counterRequest) {
		this.from = from;
		this.to = to;
		this.fromItems = fromItems;
		this.toItems = toItems;
		this.dateInitiated = dateInitiated;
		this.status = status;
		this.methodFrom = methodTo;
		this.counterRequest = counterRequest;
	}

	/*
	 * Getters / Setters
	 */
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public TradeMethod getMethodFrom() {
		return methodFrom;
	}

	public void setMethodFrom(TradeMethod methodFrom) {
		this.methodFrom = methodFrom;
	}

	public TradeMethod getMethodTo() {
		return methodTo;
	}

	public void setMethodTo(TradeMethod methodTo) {
		this.methodTo = methodTo;
	}

	public boolean isCounterRequest() {
		return counterRequest;
	}

	public void setCounterRequest(boolean counterRequest) {
		this.counterRequest = counterRequest;
	}

	public List<String> getFromItems() {
		return fromItems;
	}

	public void setFromItems(List<String> fromItems) {
		this.fromItems = fromItems;
	}

	public List<String> getToItems() {
		return toItems;
	}

	public void setToItems(List<String> toItems) {
		this.toItems = toItems;
	}

	public Date getDateInitiated() {
		return dateInitiated;
	}

	public void setDateInitiated(Date dateInitiated) {
		this.dateInitiated = dateInitiated;
	}

	public TradeRequestStatus getStatus() {
		return status;
	}

	public void setStatus(TradeRequestStatus status) {
		this.status = status;
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
		result = prime * result + ((dateInitiated == null) ? 0 : dateInitiated.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((fromItems == null) ? 0 : fromItems.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((toItems == null) ? 0 : toItems.hashCode());
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
		TradeRequest other = (TradeRequest) obj;
		if (dateInitiated == null) {
			if (other.dateInitiated != null)
				return false;
		} else if (!dateInitiated.equals(other.dateInitiated))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (fromItems == null) {
			if (other.fromItems != null)
				return false;
		} else if (!fromItems.equals(other.fromItems))
			return false;
		if (status != other.status)
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (toItems == null) {
			if (other.toItems != null)
				return false;
		} else if (!toItems.equals(other.toItems))
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
		return "TradeRequest [from=" + from + ", to=" + to + ", fromItems=" + fromItems + ", toItems=" + toItems + ", dateInitiated=" + dateInitiated + ", status=" + status + "]";
	}
	
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
	
	public enum TradeMethod {
		DROP_OFF(0),
		PICK_UP(1),
		CONTACT(2);
		
		private int value;
		
		private TradeMethod( int val ) {
			this.value = val;
		}
		
		public int getStatus() {
			return this.value;
		}
		public static String[] names() {
		    TradeMethod[] methods = values();
		    String[] names = new String[methods.length];

		    for (int i = 0; i < methods.length; i++) {
		        names[i] = methods[i].name();
		    }

		    return names;
		}
	}
}
