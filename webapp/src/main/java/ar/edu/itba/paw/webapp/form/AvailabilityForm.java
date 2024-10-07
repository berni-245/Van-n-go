package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ArrayAllMatch;
import ar.edu.itba.paw.webapp.validation.ArrayAllMax;
import ar.edu.itba.paw.webapp.validation.ArrayAllMin;

import javax.validation.constraints.NotEmpty;

public class AvailabilityForm {
    @NotEmpty
    @ArrayAllMin(1)
    @ArrayAllMax(7)
    private int[] weekDays;

    @ArrayAllMatch(regexp = "\\d{2}:00:00")
    private String[] hourBlocks;

    @NotEmpty
    private long[] vehicleIds;

    @NotEmpty
    private long[] zoneIds;

    public int[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(int[] weekDays) {
        this.weekDays = weekDays;
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

    public String[] getHourBlocks(){
        return hourBlocks;
    }

    public void setHourBlocks(String[] hourBlocks){
        this.hourBlocks = hourBlocks;
    }
}