package ar.edu.itba.paw.exceptions;

public class InvalidRecipientException extends RuntimeException {
    public InvalidRecipientException() {
        super("Invalid recipient");
    }
}
