package ar.edu.itba.paw.exceptions;

public class InvalidUserOnBookingRejectException extends RuntimeException {
    public InvalidUserOnBookingRejectException() {
        super("The booking you are trying to reject is not yours\n");
    }
}
