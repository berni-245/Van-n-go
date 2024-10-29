package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.ShiftPeriod;

import javax.validation.constraints.NotEmpty;
import java.time.DayOfWeek;

public class AvailabilityForm {
    @NotEmpty
    private DayOfWeek[] weekDays;

    @NotEmpty
    ShiftPeriod[] shiftPeriods;

    public DayOfWeek[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(DayOfWeek[] weekDays) {
        this.weekDays = weekDays;
    }

    public ShiftPeriod[] getShiftPeriods() {
        return shiftPeriods;
    }

    public void setShiftPeriods(ShiftPeriod[] shiftPeriods) {
        this.shiftPeriods = shiftPeriods;
    }
}