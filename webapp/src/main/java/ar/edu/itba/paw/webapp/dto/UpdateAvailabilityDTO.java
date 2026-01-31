package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.ShiftPeriod;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateAvailabilityDTO {
    private List<Integer> zones;
    private List<WeekTimeSlotDTO> timeSlots;

    public UpdateAvailabilityDTO() {

    }

    public List<Integer> getZones() {
        return zones;
    }

    public void setZones(List<Integer> zones) {
        this.zones = zones;
    }

    public List<WeekTimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<WeekTimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public ShiftPeriod[] getDayTimeSlots(DayOfWeek dayOfWeek) {
        Set<ShiftPeriod> dayOfWeekTimeSlots = new HashSet<>();
        for (WeekTimeSlotDTO timeSlot : timeSlots) {
            if (dayOfWeek.name().equals(timeSlot.getDay())) {
                dayOfWeekTimeSlots.add(ShiftPeriod.valueOf(timeSlot.getShift()));
            }
        }
        return dayOfWeekTimeSlots.toArray(new ShiftPeriod[0]);
    }

}
