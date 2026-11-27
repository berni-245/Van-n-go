package ar.edu.itba.paw.exceptions;

public class PlateNumberAlreadyExistsException extends RuntimeException {
    public PlateNumberAlreadyExistsException() {
        super("Plate number already exists");
    }
}
