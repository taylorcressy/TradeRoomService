/**
 * Document template for error codes
 */
package database_entities.utilities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StatusMessagesAndCodes {
	
	@Id
	private Integer code;
	private String message;
	
	public StatusMessagesAndCodes(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
