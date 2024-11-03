package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.Zone;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface VehicleDao {
    Vehicle create(
            long driverId,
            String plateNumber,
            double volume,
            String description,
            List<Zone> zones,
            double rate
    );

    Optional<Vehicle> findById(long id);

    Optional<Vehicle> findByPlateNumber(Driver driver, String plateNumber);

    List<Vehicle> getDriverVehicles(Driver driver);

    List<Vehicle> getDriverVehicles(Driver driver, Zone zone, Size size, Double minPrice, Double maxPrice, DayOfWeek weekday);

    boolean plateNumberExists(String plateNumber);

    void updateVehicle(Vehicle vehicle);

    void deleteVehicle(Vehicle vehicle);

    List<Vehicle> getDriverVehicles(Driver driver, int offset);

    long getVehicleCount(Driver driver);
}