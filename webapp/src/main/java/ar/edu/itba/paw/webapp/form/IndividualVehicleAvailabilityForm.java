package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.models.WeeklyAvailability;
import ar.edu.itba.paw.webapp.validation.ArrayAllMatch;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

public class IndividualVehicleAvailabilityForm {
    @Min(0)
    @Max(6)
    private int weekDay;

    @ArrayAllMatch(regexp = "\\d{2}:00:00")
    private String[] hourBlocks;

    private long zoneId;

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public long getZoneId() {
        return zoneId;
    }

    public void setZoneId(long zoneId) {
        this.zoneId = zoneId;
    }

    public String[] getHourBlocks() {
        return hourBlocks;
    }

    public void setHourBlocks(String[] hourBlocks) {
        this.hourBlocks = hourBlocks;
    }

    public void setAll(Vehicle vehicle) {
        Optional<WeeklyAvailability> a = vehicle.getWeeklyAvailability().stream().findFirst();
        if (a.isPresent()) {
            WeeklyAvailability b = a.get();
            weekDay = b.getWeekDay();
            zoneId = b.getZoneId();
            hourBlocks = vehicle.getWeeklyAvailability().stream().filter(wa -> wa.getWeekDay() == weekDay && wa.getZoneId() == zoneId)
                    .map((weeklyAvailability -> weeklyAvailability.getHourInterval().getStartHourString()))
                    .toArray(String[]::new);
        }
    }
}