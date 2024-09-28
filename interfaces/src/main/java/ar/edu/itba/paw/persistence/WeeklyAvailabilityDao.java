package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.WeeklyAvailability;

import java.util.List;
import java.util.Optional;

public interface WeeklyAvailabilityDao {
    Optional<WeeklyAvailability> create(int weekDay, String timeStart, String timeEnd, long zoneId, long vehicleId);

    List<WeeklyAvailability> getDriverWeeklyAvailability(long driverId);

    List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId);

    List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId, long zoneId);
}