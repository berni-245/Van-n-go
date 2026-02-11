package ar.edu.itba.paw.webapp.dto.booking;

import javax.validation.constraints.NotNull;

public class UpdateBookingStateDTO {
    @NotNull
    private BookingStateDTO state;

    public UpdateBookingStateDTO() {
    }

    public BookingStateDTO getState() {
        return state;
    }

    public void setState(BookingStateDTO state) {
        this.state = state;
    }

    public enum BookingStateDTO {
        ACCEPTED,
        REJECTED,
        FINISHED,
        CANCELED
    }
}
