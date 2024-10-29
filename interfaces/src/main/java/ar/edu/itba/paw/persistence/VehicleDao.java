package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.Zone;

import java.util.List;
import java.util.Optional;

public interface VehicleDao {
    Vehicle create(long driverId, String plateNumber, double volume, String description, double rate);

    Optional<Vehicle> findById(long id);

    Optional<Vehicle> findByPlateNumber(long driverId, String plateNumber);

    List<Vehicle> getDriverVehicles(long driverId);

    List<Vehicle> getDriverVehicles(long driverId, Zone zone, Size size);

    boolean plateNumberExists(String plateNumber);

    boolean updateVehicle(long driverId, Vehicle vehicle);
}