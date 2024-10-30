package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.ShiftPeriod;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingForm {
    @NotNull
    private String date;

    @NotEmpty
    private long vehicleId;

    @NotEmpty
    private long zoneId;

    @NotNull
    private String timeStart;

    @NotNull
    private String timeEnd;

    @NotNull
    private String shiftPeriod;

    @Length(min = 20, max = 500)
    private String jobDescription;

    public String getDate() {
        return date;
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

    public @NotNull String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(@NotNull String timeStart) {
        this.timeStart = timeStart;
    }

    public @NotNull String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(@NotNull String timeEnd) {
        this.timeEnd = timeEnd;
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
}