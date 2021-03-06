package database_entities.exceptions;

public class StatusMessageDoesNotExist extends RuntimeException {
	
	
	/**
	 * auto-generated by Eclipse
	 */
	private static final long serialVersionUID = 6161817372859757137L;
	private String message = null;
	 
    public StatusMessageDoesNotExist() {
        super();
    }
 
    public StatusMessageDoesNotExist(String message) {
        super(message);
        this.message = message;
    }
 
    public StatusMessageDoesNotExist(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
