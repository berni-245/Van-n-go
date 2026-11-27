package ar.edu.itba.paw.webapp.dto.booking;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UpdateBookingStateDTO {
    @NotNull
    @Pattern(regexp = "ACCEPTED|REJECTED|FINISHED|CANCELED")
    private String state;

    public UpdateBookingStateDTO() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
