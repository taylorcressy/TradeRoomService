package database_entities;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LastUpdated extends DatabaseDocument{

	private Date lastUpdated;

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		return "LastUpdated [id=" + id + ", lastUpdated=" + lastUpdated + "]";
	}
}
