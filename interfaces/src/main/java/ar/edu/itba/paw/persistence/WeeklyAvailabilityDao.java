package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.WeeklyAvailability;

import java.util.List;

public interface WeeklyAvailabilityDao {
    int create(int weekDay, String[] hourBlocks, long zoneId, long vehicleId);

    public void removeAll(int weekDay, long zoneId, long vehicleId);

    List<WeeklyAvailability> getDriverWeeklyAvailability(long driverId);

    List<WeeklyAvailability> getDriverWeeklyAvailability(long driverId, long zoneId, Size size);

    List<List<WeeklyAvailability>> getDriverWeeklyAvailabilityByDays(long driverId);

    List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId);

    List<WeeklyAvailability> getVehicleWeeklyAvailability(long vehicleId, long zoneId);

}