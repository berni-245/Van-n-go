package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;

import java.util.Optional;

public interface DriverService {
    Optional<Driver> findById(long id);
    Vehicle addVehicle(long driverId, String plateNumber, double volume, String description);
    Driver create(String username, String mail, String extra1);
}