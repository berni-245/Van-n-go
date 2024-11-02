package ar.edu.itba.paw.exceptions;

public class VehicleIsAlreadyAcceptedException extends RuntimeException{
    public VehicleIsAlreadyAcceptedException(){
        super("Vehicle already accepted for that shift period\n");
    }
}
