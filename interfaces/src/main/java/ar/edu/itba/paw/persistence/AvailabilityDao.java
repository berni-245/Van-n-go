package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.Vehicle;

import java.time.DayOfWeek;
import java.util.Map;

public interface AvailabilityDao {
    Availability create(Vehicle vehicle, DayOfWeek weekDay, ShiftPeriod period);

    void updateVehicleAvailability(Vehicle vehicle, Map<DayOfWeek, ShiftPeriod[]> periods);
}