package database_entities;

public class Rank {
	
	private static final int MAX_RANK = 5;
	private static final int MIN_RANK = 1;
	
	private String id;
	private Integer rank;
	private String comment;
	private String owner;
	
	public Rank(String id, Integer rank, String comment, String owner) {
		this.rank = rank;
		this.comment = comment;
		this.owner = owner;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		if(rank > MAX_RANK) 
			this.rank = MAX_RANK;
		else if(rank < MIN_RANK)
			this.rank = MIN_RANK;
		
		this.rank = rank;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Rank [rank=" + rank + ", comment=" + comment + ", owner=" + owner + "]";
	}
}
