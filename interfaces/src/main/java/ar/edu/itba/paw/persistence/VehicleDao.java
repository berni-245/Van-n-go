package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Vehicle;

import java.util.Optional;

public interface VehicleDao {
    Vehicle create(long driverId, String plateNumber, double volume, String description);
    Optional<Vehicle> findById(long id);
    Optional<Vehicle> findByPlateNumber(String plateNumber);
}