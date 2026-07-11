package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class DriverDTO extends UserDTO {

    private Double rating;
    private String cbu;
    private String description;
    private URI vehicles;

    public static Function<Driver, DriverDTO> mapper(UriInfo uriInfo) {
        return (driver) -> fromPublicDriverInfo(uriInfo, driver);
    }

    public static DriverDTO fromPublicDriverInfo(UriInfo uriInfo, Driver driver) {
        DriverDTO dto = DriverDTO.baseDriverDtoBuilder(uriInfo, driver);
        dto.setPublicInfoFromUser(uriInfo, driver);
        return dto;
    }

    public static DriverDTO fromDriver(UriInfo uriInfo, Driver driver) {
        DriverDTO dto = DriverDTO.baseDriverDtoBuilder(uriInfo, driver);
        dto.setAllInfoFromUser(uriInfo, driver);
        return dto;
    }

    private static DriverDTO baseDriverDtoBuilder(UriInfo uriInfo, Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setRating(driver.getRating());
        dto.setCbu(driver.getCbu());
        dto.setDescription(driver.getDescription());
        dto.vehicles = UriUtils.driverVehiclesUri(uriInfo, driver.getId()).build();
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
