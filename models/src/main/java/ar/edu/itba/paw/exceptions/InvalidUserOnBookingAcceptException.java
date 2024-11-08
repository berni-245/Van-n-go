package ar.edu.itba.paw.exceptions;

public class InvalidUserOnBookingAcceptException extends RuntimeException {
    public InvalidUserOnBookingAcceptException() {
        super("The booking you are trying to accept is not yours\n");
    }
}
