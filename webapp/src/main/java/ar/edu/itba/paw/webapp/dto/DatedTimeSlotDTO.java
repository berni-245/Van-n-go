package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Booking;

public class DatedTimeSlotDTO {
    private String date;
    private String shift;

    public DatedTimeSlotDTO() {

    }

    public static DatedTimeSlotDTO fromBooking(Booking booking) {
        DatedTimeSlotDTO dto = new DatedTimeSlotDTO();
        dto.date = booking.getDate().toString();
        dto.shift = booking.getShiftPeriod().name();
        return dto;
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
