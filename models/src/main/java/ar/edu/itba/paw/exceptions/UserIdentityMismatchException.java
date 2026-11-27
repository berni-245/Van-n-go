package ar.edu.itba.paw.exceptions;

public class UserIdentityMismatchException extends RuntimeException {
    public UserIdentityMismatchException() {
        super("The resource you are trying to access belongs to another user");
    }
}
