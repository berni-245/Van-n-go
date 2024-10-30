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
    private long zoneId;

    @NotNull
    private String shiftPeriod;

    @NotNull
    private long destinationId;

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
    public long getZoneId() {
        return zoneId;
    }

    public void setZoneId(@NotEmpty long zoneId) {
        this.zoneId = zoneId;
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
    public long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(@NotNull long destinationId) {
        this.destinationId = destinationId;
    }
}