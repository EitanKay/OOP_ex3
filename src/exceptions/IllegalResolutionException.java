package exceptions;

/**
 * This exception is thrown when attempting to set resolution to an illegal value.
 * It extends RuntimeException, indicating that it is an unchecked exception.
 *
 * @author Eitan Kayesar and Ariel Monzon
 */
public class IllegalResolutionException extends RuntimeException {
    public IllegalResolutionException(String message) {
        super(message);
    }
}
