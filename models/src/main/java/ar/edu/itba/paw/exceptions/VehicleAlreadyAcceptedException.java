package ar.edu.itba.paw.exceptions;

public class VehicleAlreadyAcceptedException extends Exception{
    public VehicleAlreadyAcceptedException(){
        super("Vehicle already accepted for that shift period\n");
    }
}
