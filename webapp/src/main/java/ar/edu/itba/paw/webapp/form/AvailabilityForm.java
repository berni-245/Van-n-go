package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.Vehicle;

import java.time.DayOfWeek;

public class AvailabilityForm {
    ShiftPeriod[] mondayShiftPeriods;
    ShiftPeriod[] tuesdayShiftPeriods;
    ShiftPeriod[] wednesdayShiftPeriods;
    ShiftPeriod[] thursdayShiftPeriods;
    ShiftPeriod[] fridayShiftPeriods;
    ShiftPeriod[] saturdayShiftPeriods;
    ShiftPeriod[] sundayShiftPeriods;

    public void setAll(Vehicle vehicle) {
        mondayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.MONDAY);
        tuesdayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.TUESDAY);
        wednesdayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.WEDNESDAY);
        thursdayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.THURSDAY);
        fridayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.FRIDAY);
        saturdayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.SATURDAY);
        sundayShiftPeriods = getDayShiftPeriods(vehicle, DayOfWeek.SUNDAY);
    }

    private ShiftPeriod[] getDayShiftPeriods(Vehicle vehicle, DayOfWeek day) {
        return vehicle.getAvailability().stream().filter(
                wa -> wa.getWeekDay() == day
        ).map(Availability::getShiftPeriod).toArray(ShiftPeriod[]::new);
    }

    public ShiftPeriod[] getMondayShiftPeriods() {
        return mondayShiftPeriods;
    }

    public void setMondayShiftPeriods(ShiftPeriod[] mondayShiftPeriods) {
        this.mondayShiftPeriods = mondayShiftPeriods;
    }

    public ShiftPeriod[] getTuesdayShiftPeriods() {
        return tuesdayShiftPeriods;
    }

    public void setTuesdayShiftPeriods(ShiftPeriod[] tuesdayShiftPeriods) {
        this.tuesdayShiftPeriods = tuesdayShiftPeriods;
    }

    public ShiftPeriod[] getWednesdayShiftPeriods() {
        return wednesdayShiftPeriods;
    }

    public void setWednesdayShiftPeriods(ShiftPeriod[] wednesdayShiftPeriods) {
        this.wednesdayShiftPeriods = wednesdayShiftPeriods;
    }

    public ShiftPeriod[] getThursdayShiftPeriods() {
        return thursdayShiftPeriods;
    }

    public void setThursdayShiftPeriods(ShiftPeriod[] thursdayShiftPeriods) {
        this.thursdayShiftPeriods = thursdayShiftPeriods;
    }

    public ShiftPeriod[] getFridayShiftPeriods() {
        return fridayShiftPeriods;
    }

    public void setFridayShiftPeriods(ShiftPeriod[] fridayShiftPeriods) {
        this.fridayShiftPeriods = fridayShiftPeriods;
    }

    public ShiftPeriod[] getSaturdayShiftPeriods() {
        return saturdayShiftPeriods;
    }

    public void setSaturdayShiftPeriods(ShiftPeriod[] saturdayShiftPeriods) {
        this.saturdayShiftPeriods = saturdayShiftPeriods;
    }

    public ShiftPeriod[] getSundayShiftPeriods() {
        return sundayShiftPeriods;
    }

    public void setSundayShiftPeriods(ShiftPeriod[] sundayShiftPeriods) {
        this.sundayShiftPeriods = sundayShiftPeriods;
    }
}