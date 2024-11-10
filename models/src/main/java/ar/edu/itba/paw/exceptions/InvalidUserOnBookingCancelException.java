package ar.edu.itba.paw.exceptions;

public class InvalidUserOnBookingCancelException extends RuntimeException {
    public InvalidUserOnBookingCancelException() {
        super("The booking you are trying to cancel is not yours");
    }
}
