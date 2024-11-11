package ar.edu.itba.paw.exceptions;

public class InvalidVehicleException extends RuntimeException {
    public InvalidVehicleException() {
        super("Invalid vehicle");
    }
}
