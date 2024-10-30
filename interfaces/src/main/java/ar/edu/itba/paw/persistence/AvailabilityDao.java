package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.Vehicle;

import java.time.DayOfWeek;
import java.util.Map;

public interface AvailabilityDao {
    public Availability create(Vehicle vehicle, DayOfWeek weekDay, ShiftPeriod period);

    public void updateVehicleAvailability(Vehicle vehicle, Map<DayOfWeek, ShiftPeriod[]> periods);

//    public void removeAll(int weekDay, long zoneId, long vehicleId);

//    List<Availability> getDriverWeeklyAvailability(long driverId);
//
//    List<Availability> getDriverWeeklyAvailability(long driverId, long zoneId, Size size);
//
//    List<List<Availability>> getDriverWeeklyAvailabilityByDays(long driverId);
//
//    List<Availability> getVehicleWeeklyAvailability(long vehicleId);
//
//    List<Availability> getVehicleWeeklyAvailability(long vehicleId, long zoneId);
//
//    List<Availability> getVehicleActiveAvailability(long vehicleId, long zoneId, LocalDate date);
}