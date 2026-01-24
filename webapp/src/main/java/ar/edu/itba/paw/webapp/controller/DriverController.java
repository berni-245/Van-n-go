package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.SearchOrder;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.webapp.dto.DriverDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.DayOfWeek;
import java.util.List;

@Path("/api/drivers")
@Component
public class DriverController {

    @Context
    private UriInfo uriInfo;

    private final DriverService ds;

    @Autowired
    public DriverController(DriverService ds) {
        this.ds = ds;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response listDrivers(
            @QueryParam("originZoneId") int zoneId,
            @QueryParam("size") Size size,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("weekDay") DayOfWeek weekDay,
            @QueryParam("minRating") Integer minRating,
            @QueryParam("sortOrder") @DefaultValue("ALPHABETICAL") SearchOrder order,
            @QueryParam("page") @DefaultValue("1") int page
        ) {
        System.out.println(minRating == null ? "Null" : "Not null");
        List<DriverDTO> drivers = ds
                .getSearchResults(zoneId, size, maxPrice, weekDay, minRating, order, page)
                .stream().map(DriverDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(drivers) {}).build();
    }


}
