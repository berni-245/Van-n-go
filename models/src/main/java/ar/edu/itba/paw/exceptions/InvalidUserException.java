package ar.edu.itba.paw.exceptions;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() {
        super("user doesn't exist");
    }
}
