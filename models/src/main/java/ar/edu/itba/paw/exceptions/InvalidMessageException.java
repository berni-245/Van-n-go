package ar.edu.itba.paw.exceptions;

public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException() {
        super("Invalid message. Should be 255 characters or less");
    }
}
