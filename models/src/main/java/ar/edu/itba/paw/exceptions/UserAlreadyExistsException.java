package ar.edu.itba.paw.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("Username already exists");
    }
}
