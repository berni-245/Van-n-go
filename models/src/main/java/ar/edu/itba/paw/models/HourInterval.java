package ar.edu.itba.paw.models;

import java.time.LocalTime;

public class HourInterval {
    private final int startHour;
    private final int endHour;
    private final int hourCount;

    public HourInterval(int startHour, int endHour) {
        if(endHour == 0)
            endHour = 24;
        if(! ((0 <= startHour) && (startHour < endHour) && (endHour <= 24)) )
            throw new IllegalArgumentException("Start time must be before end time");
        this.startHour = startHour;
        this.endHour = endHour;
        this.hourCount = endHour - startHour;
    }

    // Only uses the hours for interval
    public HourInterval(LocalTime startTime, LocalTime endTime) {
        this(startTime.getHour(), endTime.getHour());
    }

    // Only uses the hours for interval
    public HourInterval(String startTime, String endTime) {
        this(LocalTime.parse(startTime), LocalTime.parse(endTime));
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getHourCount() {
        return hourCount;
    }

    public int getStartHourBlockId() {
        return startHour + 1;
    }

    public int getEndHourBlockId() {
        return endHour;
    }
}
