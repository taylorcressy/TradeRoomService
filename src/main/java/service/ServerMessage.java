package service;

public class ServerMessage {
	
	private Object data;
	private String message;
	private int code;
	
	
	public ServerMessage() {
		//Empty
	}
	
	/**
	 * The Primary Constructor - Server message will be set automatically
	 * 
	 * @param data - The data (Usually JSON) to pass to the client
	 * @param code - the integer code to specify the result of a request
	 * @param message - the message to pass back to the client
	 */
	public ServerMessage(int code, Object data, String message) {
		this.code = code;
		this.data = data;
		this.message = message;
	}
	
	/*
	 * Getters / Setters
	 */
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * toString method
	 * @return String
	 */
	public String toString() {
		String retString = "Server Message: " + this.message + "(Status Code: " + this.code+ ")\n";
		if(this.data != null) 
			retString += "\t Data: " + this.data;
		
		return retString;
	}
	
}
