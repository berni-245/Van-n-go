package ar.edu.itba.paw.webapp.dto;

public class WeekTimeSlotDTO {
    private String day;
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
