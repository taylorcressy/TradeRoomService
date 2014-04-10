/**
 * Entity interface for use with MongoDB Data / Spring
 * Defines the information associated with a Trade Request
 * 
 * @author Taylor Cressy
 * @since April 9, 2014
 * @version 1.0
 */
package database_handler.entities;

import java.sql.Timestamp;
import java.util.List;

public class TradeRequest {

	private String from;
	private String to;
	private List<TradeItem> fromItems;
	private List<TradeItem> toItems;
	private Address preferredMeetingPlace;
	private Timestamp dateInitiated;
	private TradeRequestStatus status;

	/*
	 * Constructor
	 */
	public TradeRequest(String from, String to, List<TradeItem> fromItems, List<TradeItem> toItems, Address preferredMeetingPlace,
			Timestamp dateInit, TradeRequestStatus status) {
		this.from = from;
		this.to = to;
		this.fromItems = fromItems;
		this.toItems = toItems;
		this.preferredMeetingPlace = preferredMeetingPlace;
		this.dateInitiated = dateInit;
		this.status = status;
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

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public List<TradeItem> getFromItems() {
		return fromItems;
	}

	public void setFromItems(List<TradeItem> fromItems) {
		this.fromItems = fromItems;
	}

	public List<TradeItem> getToItems() {
		return toItems;
	}

	public void setToItems(List<TradeItem> toItems) {
		this.toItems = toItems;
	}

	public Address getPreferredMeetingPlace() {
		return preferredMeetingPlace;
	}

	public void setPreferredMeetingPlace(Address preferredMeetingPlace) {
		this.preferredMeetingPlace = preferredMeetingPlace;
	}

	public Timestamp getDateInitiated() {
		return dateInitiated;
	}

	public void setDateInitiated(Timestamp dateInitiated) {
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
		result = prime * result + ((preferredMeetingPlace == null) ? 0 : preferredMeetingPlace.hashCode());
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
		if (preferredMeetingPlace == null) {
			if (other.preferredMeetingPlace != null)
				return false;
		} else if (!preferredMeetingPlace.equals(other.preferredMeetingPlace))
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
		return "TradeRequest [from=" + from + ", to=" + to + ", fromItems=" + fromItems + ", toItems=" + toItems + ", preferredMeetingPlace="
				+ preferredMeetingPlace + ", dateInitiated=" + dateInitiated + ", status=" + status + "]";
	}
}
