package ar.edu.itba.paw.exceptions;

public class InvalidUserOnBookingFinishException extends RuntimeException {
    public InvalidUserOnBookingFinishException() {
        super("The booking you are trying to finish is not yours\n");
    }
}
