package ar.edu.itba.paw.exceptions;

import ar.edu.itba.paw.models.Driver;

public class DriverVehicleLimitReachedException extends RuntimeException{
    public DriverVehicleLimitReachedException(Driver driver){
        super("Driver %d reached maximum number of vehicles".formatted(driver.getId()));
    }
}
