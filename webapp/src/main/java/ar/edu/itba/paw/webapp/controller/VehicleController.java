package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.VehicleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.DayOfWeek;
import java.util.List;

@Path("/api/drivers/{driverId}/vehicles")
@Component
public class VehicleController {

    @Context
    private UriInfo uriInfo;

    private final DriverService ds;

    private final ImageService imageService;

    @Autowired
    public VehicleController(DriverService ds, ImageService imageService) {
        this.ds = ds;
        this.imageService = imageService;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getVehicles(
            @PathParam("driverId") int driverId,
            @QueryParam("zoneId") Integer zoneId,
            @QueryParam("size") Size size,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("weekDay") DayOfWeek weekDay,
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        Driver driver = ds.findById(driverId);
        List<Vehicle> vehiclesFromModel;
        if (zoneId == null)
            vehiclesFromModel = ds.getVehicles(driver, page);
        else
            vehiclesFromModel = ds.getVehicles(driver, zoneId, size, maxPrice, weekDay);
        List<VehicleDTO> vehicles = vehiclesFromModel.stream().map(VehicleDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(vehicles) {
        }).build();
    }
}
