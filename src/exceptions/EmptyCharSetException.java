package exceptions;

/**
 * This exception is thrown when an operation is attempted on a character set that is empty.
 * It extends RuntimeException, indicating that it is an unchecked exception.
 *
 * @author Eitan Kayesar and Ariel Monzon
 */
public class EmptyCharSetException extends RuntimeException{
	public EmptyCharSetException(String message){
		super(message);
	}
}
