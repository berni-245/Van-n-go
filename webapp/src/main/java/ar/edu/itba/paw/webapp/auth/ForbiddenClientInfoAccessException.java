package ar.edu.itba.paw.webapp.auth;

public class ForbiddenClientInfoAccessException extends RuntimeException {
    public ForbiddenClientInfoAccessException() {
        super("The authenticated user must be a driver and have a booking with the requested client");
    }
}
