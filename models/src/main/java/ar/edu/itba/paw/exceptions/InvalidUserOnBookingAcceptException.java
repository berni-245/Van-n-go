package ar.edu.itba.paw.exceptions;

public class InvalidUserOnBookingAcceptException extends RuntimeException {
    public InvalidUserOnBookingAcceptException() {
        super("toast.invalid.user.booking.accept=The booking you are trying to accept is not yours\n");
    }
}
