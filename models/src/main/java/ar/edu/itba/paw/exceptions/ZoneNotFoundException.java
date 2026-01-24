package ar.edu.itba.paw.exceptions;

public class ZoneNotFoundException extends RuntimeException {
    public ZoneNotFoundException() {
        super("Zone not found");
    }
}
