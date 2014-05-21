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
	
	private String senderUsername;
	private String receiverUsername;
	private FriendRequestStatus status;
	
	public FriendRequest(String senderUsername, String receiverUsername, FriendRequestStatus status) {
		this.senderUsername = senderUsername;
		this.receiverUsername = receiverUsername;
		this.status = status;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public String getReceiverUsername() {
		return receiverUsername;
	}

	public void setReceiverUsername(String receiverUsername) {
		this.receiverUsername = receiverUsername;
	}

	public FriendRequestStatus getStatus() {
		return status;
	}

	public void setStatus(FriendRequestStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "FriendRequest [senderId=" + senderUsername + ", receiverId=" + receiverUsername + ", status=" + status + "]";
	}
}
