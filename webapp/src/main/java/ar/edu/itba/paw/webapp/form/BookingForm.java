package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingForm {
    @NotNull
    private String date;

    @NotEmpty
    private long vehicleId;

    @NotEmpty
    private long originZoneId;

    @NotNull
    private String shiftPeriod;

    @NotEmpty
    private long destinationZoneId;

    @Length(min = 20, max = 500)
    private String jobDescription;

    public LocalDate getDate() {
        return LocalDate.parse(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NotEmpty
    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(@NotEmpty long vehicleId) {
        this.vehicleId = vehicleId;
    }

    @NotEmpty
    public long getOriginZoneId() {
        return originZoneId;
    }

    public void setOriginZoneId(@NotEmpty long originZoneId) {
        this.originZoneId = originZoneId;
    }

    public @Length(min = 20, max = 500) String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(@Length(min = 20, max = 500) String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public @NotNull String getShiftPeriod() {
        return shiftPeriod;
    }

    public void setShiftPeriod(@NotNull String shiftPeriod) {
        this.shiftPeriod = shiftPeriod;
    }

    @NotNull
    public long getDestinationZoneId() {
        return destinationZoneId;
    }

    public void setDestinationZoneId(@NotNull long destinationZoneId) {
        this.destinationZoneId = destinationZoneId;
    }
}