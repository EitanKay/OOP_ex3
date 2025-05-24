package exceptions;

/**
 * This exception is thrown when a user attempts to interact with the shell using invalid formatting.
 * It extends RuntimeException, indicating that it is an unchecked exception.
 *
 * @author Eitan Kayesar and Ariel Monzon
 */
public class IllegalFormatException extends RuntimeException {
	public IllegalFormatException(String message) {
		super(message);
	}
}
