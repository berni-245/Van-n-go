package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.ShiftPeriod;

import java.time.LocalDate;

public class CreateBookingDTO {
    private Integer vehicleId;
    private Integer originZoneId;
    private Integer destinationZoneId;
    private String jobDescription;
    private LocalDate date;
    private ShiftPeriod shiftPeriod;

    //TODO: sacar este atributo cuando tengamos JWT, esto es solo para testear
    private Integer clientId;

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

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
