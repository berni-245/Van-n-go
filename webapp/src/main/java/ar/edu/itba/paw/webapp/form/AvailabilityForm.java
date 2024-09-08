package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ArrayAllMax;
import ar.edu.itba.paw.webapp.validation.ArrayAllMin;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class AvailabilityForm {
    @NotEmpty
    @ArrayAllMin(1)
    @ArrayAllMax(7)
    private int[] weekDays;

    @NotBlank
    private String timeStart;

    @NotBlank
    private String timeEnd;

    @NotEmpty
    private long[] vehicleIds;

    @AssertTrue(message = "Start time cannot come after end time")
    public boolean isValidTime() {
        return timeStart != null && timeEnd != null && timeEnd.compareTo(timeStart) > 0;
    }

    @NotEmpty
    private long[] zoneIds;

    public int[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(int[] weekDays) {
        this.weekDays = weekDays;
    }

    public String getTimeStart() {
        return timeStart + ":00";
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd + ":00";
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public long[] getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(long[] vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public long[] getZoneIds() {
        return zoneIds;
    }

    public void setZoneIds(long[] zoneIds) {
        this.zoneIds = zoneIds;
    }
}