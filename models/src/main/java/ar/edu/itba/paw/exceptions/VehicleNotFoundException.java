package ar.edu.itba.paw.exceptions;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException() {
        super("Vehicle was not found");
    }
}
