package database_entities.exceptions;

public class FatalDatabaseErrorException extends RuntimeException {
	
	
	/**
	 * Auto-generated by Eclipse
	 */
	private static final long serialVersionUID = -5266446920985778441L;
	private String message = null;
	
    public FatalDatabaseErrorException() {
        super();
    }
 
    public FatalDatabaseErrorException(String message) {
        super(message);
        this.message = message;
    }
 
    public FatalDatabaseErrorException(Throwable cause) {
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