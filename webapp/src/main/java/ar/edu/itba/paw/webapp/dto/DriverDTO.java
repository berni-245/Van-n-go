package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Driver;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class DriverDTO extends UserDTO {

    private Double rating;
    private String cbu;
    private String description;
    private URI vehicles;

    public static Function<Driver, DriverDTO> mapper(UriInfo uriInfo) {
        return (driver) -> fromDriver(uriInfo, driver);
    }


    public static DriverDTO fromDriver(UriInfo uriInfo, Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setFromUser(uriInfo, driver);
        dto.setRating(driver.getRating());
        dto.setCbu(driver.getCbu());
        dto.setDescription(driver.getDescription());
        dto.vehicles = uriInfo.getBaseUriBuilder()
                .path("drivers").path(String.valueOf(driver.getId()))
                .path("vehicles").build();
        return dto;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getVehicles() {
        return vehicles;
    }

    public void setVehicles(URI vehicles) {
        this.vehicles = vehicles;
    }
}
