package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.WeeklyAvailability;

import java.util.List;
import java.util.Optional;

public interface DriverService {
    Driver create(String username, String mail, String password, String extra1);

    Optional<Driver> findById(long id);

    Vehicle addVehicle(long driverId, String plateNumber, double volume, String description);

    List<Vehicle> getVehicles(long id);

    List<WeeklyAvailability> getWeeklyAvailability(long id);

    List<WeeklyAvailability> addWeeklyAvailability(
            long driverId,
            int[] weekDays,
            String timeStart,
            String timeEnd,
            long[] zoneIds,
            long[] vehicleIds
    );

    List<Driver> getAll();

    List<Driver> getAll(long zoneId);

    List<Driver> getAll(long zoneId, Size size);
}