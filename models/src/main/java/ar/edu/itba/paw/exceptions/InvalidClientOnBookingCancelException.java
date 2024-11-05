package ar.edu.itba.paw.exceptions;

public class InvalidClientOnBookingCancelException extends RuntimeException {
    public InvalidClientOnBookingCancelException() {
        super("The client already appointed for that vehicle at that zone and time\n");
    }
}
