package ar.edu.itba.paw.exceptions;

public class VehicleNotAvailableException extends RuntimeException{
    public VehicleNotAvailableException(){
        super("The Vehicle is not available at that time");
    }
}
