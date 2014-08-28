package utils.validators.dto;

public class Error {

	private final String key;
	private final String message;
	
	public Error( final String key , final String message ){
		this.key = key;
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public String getMessage() {
		return message;
	}

}
