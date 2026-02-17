package ar.edu.itba.paw.exceptions;

public class UserIdNotProvidedException extends RuntimeException {
    public UserIdNotProvidedException() {
        super("A User id needs to be provided");
    }
}
