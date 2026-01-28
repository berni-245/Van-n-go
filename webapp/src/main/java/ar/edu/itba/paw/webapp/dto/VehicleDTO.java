package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Vehicle;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class VehicleDTO {
    private int id;
    private String plateNumber;
    private double volumeM3;
    private double hourlyRate;
    private String description;
    private URI driver;
    private URI picture;
    private URI self;
    private URI availability;
    private URI bookedTimes;

    public static Function<Vehicle, VehicleDTO> mapper(UriInfo uriInfo) {
        return (vehicle) -> fromVehicle(uriInfo, vehicle);
    }

    public static VehicleDTO fromVehicle(UriInfo uriInfo, Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setVolumeM3(vehicle.getVolume());
        dto.setHourlyRate(vehicle.getHourlyRate());
        dto.setDescription(vehicle.getDescription());
        UriBuilder baseDriverURI = uriInfo.getBaseUriBuilder()
                .path("drivers")
                .path(String.valueOf(vehicle.getDriver().getId()));
        UriBuilder baseVehicleURI = baseDriverURI.clone()
                .path("vehicles")
                .path(String.valueOf(vehicle.getId()));
        dto.driver = baseDriverURI.clone().build();
        dto.self = baseVehicleURI.clone().build();
        dto.picture = baseVehicleURI.clone().path("picture").build();
        dto.availability = baseVehicleURI.clone().path("availability").build();
        dto.bookedTimes = baseVehicleURI.clone().path("booked-times").build();

        return dto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public URI getDriver() {
        return driver;
    }

    public void setDriver(URI driver) {
        this.driver = driver;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public double getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(double volumeM3) {
        this.volumeM3 = volumeM3;
    }

    public URI getPicture() {
        return picture;
    }

    public void setPicture(URI picture) {
        this.picture = picture;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getAvailability() {
        return availability;
    }

    public void setAvailability(URI availability) {
        this.availability = availability;
    }

    public URI getBookedTimes() {
        return bookedTimes;
    }

    public void setBookedTimes(URI bookedTimes) {
        this.bookedTimes = bookedTimes;
    }
}