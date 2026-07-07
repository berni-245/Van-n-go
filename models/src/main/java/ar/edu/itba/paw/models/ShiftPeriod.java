package ar.edu.itba.paw.models;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public enum ShiftPeriod {
    MORNING("morning", 12),
    AFTERNOON("afternoon", 19),
    EVENING("evening", 24);

    private final String lowerCaseText;

    private final int finalHour;

    ShiftPeriod(String lowerCaseText, int finalHour) {
        this.lowerCaseText = lowerCaseText;
        this.finalHour = finalHour;
    }

    public String getLowerCaseText() {
        return lowerCaseText;
    }

    public int getFinalHour() {
        return finalHour;
    }

    public static List<ShiftPeriod> getEndedShiftPeriods(LocalTime time) {
        int hour = time.getHour();

        List<ShiftPeriod> expired = new ArrayList<>();

        for (ShiftPeriod sp : ShiftPeriod.values()) {
            if (hour >= sp.getFinalHour()) {
                expired.add(sp);
            }
        }

        return expired;
    }

}
