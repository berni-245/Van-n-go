package ar.edu.itba.paw.webapp.dto;

public class DatedTimeSlotDTO {
    private String date;
    private String shift;

    public DatedTimeSlotDTO() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
