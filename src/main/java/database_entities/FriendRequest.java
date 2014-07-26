package database_entities;

public class FriendRequest {
	
	public enum FriendRequestStatus {
		ACCEPTED(1), 
		PENDING(2), 
		DENIED(3),
		BLOCKED(4);
		
		private int value;
		private FriendRequestStatus(int val) {
			this.value = val;
		}
		
		public String stringVal() {
			switch(this.value) {
			case 1:
				return "Accepted";
			case 2:
				return "Pending";
			case 3: 
				return "Denied";
			default: 
				return null;
			}				
		}
	}
	
	private String senderId;
	private String receiverId;
	private FriendRequestStatus status;
	
	public FriendRequest(String senderId, String receiverId, FriendRequestStatus status) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.status = status;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public FriendRequestStatus getStatus() {
		return status;
	}

	public void setStatus(FriendRequestStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "FriendRequest [senderId=" + senderId + ", receiverId=" + receiverId + ", status=" + status + "]";
	}
}
