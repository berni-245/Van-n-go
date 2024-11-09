package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingForm {
    @NotNull
    private String date;

    @NotNull
    private int vehicleId;

    @NotNull
    private int originZoneId;

    @NotNull
    private String shiftPeriod;

    @NotNull
    private int destinationZoneId;

    @Length(min = 1, max = 500)
    private String jobDescription;

    public LocalDate getDate() {
        return LocalDate.parse(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NotNull
    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(@NotNull int vehicleId) {
        this.vehicleId = vehicleId;
    }

    @NotNull
    public int getOriginZoneId() {
        return originZoneId;
    }

    public void setOriginZoneId(@NotNull int originZoneId) {
        this.originZoneId = originZoneId;
    }

    public @Length(min = 1, max = 500) String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(@Length(min = 1, max = 500) String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public @NotNull String getShiftPeriod() {
        return shiftPeriod;
    }

    public void setShiftPeriod(@NotNull String shiftPeriod) {
        this.shiftPeriod = shiftPeriod;
    }

    @NotNull
    public int getDestinationZoneId() {
        return destinationZoneId;
    }

    public void setDestinationZoneId(@NotNull int destinationZoneId) {
        this.destinationZoneId = destinationZoneId;
    }
}