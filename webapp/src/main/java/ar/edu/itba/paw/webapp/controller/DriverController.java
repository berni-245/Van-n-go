package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.SearchOrder;
import ar.edu.itba.paw.models.Size;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.DriverDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.time.DayOfWeek;
import java.util.List;

@Path("/api/drivers")
@Component
public class DriverController {

    @Context
    private UriInfo uriInfo;

    private final DriverService ds;

    private final ImageService imageService;

    @Autowired
    public DriverController(DriverService ds, ImageService imageService) {
        this.ds = ds;
        this.imageService = imageService;
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
        List<DriverDTO> drivers = ds
                .getSearchResults(zoneId, size, maxPrice, weekDay, minRating, order, page)
                .stream().map(DriverDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(drivers) {
        }).build();
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createDriver(DriverDTO dto) {
        Driver created = ds.create(dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getDescription(), LocaleContextHolder.getLocale());
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(DriverDTO.fromDriver(uriInfo, created)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getDriverById(@PathParam("id") int id) {
        Driver driver = ds.findById(id);
        return Response.ok(DriverDTO.fromDriver(uriInfo, driver)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response updateClientById(@PathParam("id") int id, DriverDTO dto) {
        Driver driver = ds.findById(id);
        ds.editProfile(
                driver, dto.getUsername(), dto.getEmail(), dto.getDescription(),
                dto.getCbu(), dto.getPreferredLanguage() != null ? dto.getPreferredLanguage().name() : driver.getLanguage().name() // TODO cambiar esta nastyeada luego
        );
        return Response.ok(DriverDTO.fromDriver(uriInfo, driver)).build();
    }

    @GET
    @Path("/{id}/profile-picture")
    @Produces({"image/jpeg", "image/png"})
    public Response getProfilePicture(@PathParam("id") final int id) {
        Driver driver = ds.findById(id);
        Integer imageId = driver.getPfp();
        if(imageId != null){
            byte[] pictureData = imageService.getImage(imageId).getData();
            if (pictureData != null) {
                return Response.ok(pictureData).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/profile-picture")
    @Consumes({"image/jpeg", "image/png"})
    public Response uploadProfilePicture(@PathParam("id") final int id, byte[] imageData) {
        Driver driver = ds.findById(id);
        imageService.uploadPfp(driver, imageData, "driver_" + driver.getId() + "_pfp");
        return Response.created(uriInfo.getAbsolutePathBuilder().build()).build();
    }

}
