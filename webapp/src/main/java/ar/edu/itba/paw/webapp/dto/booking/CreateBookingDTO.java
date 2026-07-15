package ar.edu.itba.paw.webapp.dto.booking;

import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.webapp.validation.FutureDate;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateBookingDTO {
    @NotNull
    private Integer vehicleId;

    @NotNull
    @ValidZoneId
    private Integer originZoneId;

    @NotNull
    @ValidZoneId
    private Integer destinationZoneId;

    @NotNull
    @Length(min = 5, max = 500)
    private String jobDescription;

    @NotNull
    @FutureDate
    private LocalDate date;

    @NotNull
    private ShiftPeriod shiftPeriod;

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getOriginZoneId() {
        return originZoneId;
    }

    public void setOriginZoneId(Integer originZoneId) {
        this.originZoneId = originZoneId;
    }

    public Integer getDestinationZoneId() {
        return destinationZoneId;
    }

    public void setDestinationZoneId(Integer destinationZoneId) {
        this.destinationZoneId = destinationZoneId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ShiftPeriod getShiftPeriod() {
        return shiftPeriod;
    }

    public void setShiftPeriod(ShiftPeriod shiftPeriod) {
        this.shiftPeriod = shiftPeriod;
    }
}
