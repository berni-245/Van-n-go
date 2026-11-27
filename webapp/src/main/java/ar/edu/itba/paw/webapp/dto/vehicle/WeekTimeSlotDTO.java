package ar.edu.itba.paw.webapp.dto.vehicle;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
public class WeekTimeSlotDTO {
    @NotNull
    @Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY")
    private String day;
    @NotNull
    @Pattern(regexp = "MORNING|AFTERNOON|EVENING", message = "{Invalid.shift}")
    private String shift;

    public WeekTimeSlotDTO() {

    }

    public WeekTimeSlotDTO(String day, String shift) {
        this.day = day;
        this.shift = shift;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
