package exceptions;

public class DuplicateRegisterException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateRegisterException(String message) {
		super(message);
	}
	
}
