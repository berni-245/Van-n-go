package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.user.CreateDriverDTO;
import ar.edu.itba.paw.webapp.dto.user.DriverDTO;
import ar.edu.itba.paw.webapp.dto.user.UpdateDriverDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import ar.edu.itba.paw.webapp.validation.ValidImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

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
    @Produces(value = {CustomMediaType.DRIVER_LIST})
    public Response listDrivers(
            @QueryParam("originZoneId") int zoneId,
            @QueryParam("size") Size size,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("weekDay") DayOfWeek weekDay,
            @QueryParam("minRating") Integer minRating,
            @QueryParam("sortOrder") @DefaultValue("ALPHABETICAL") SearchOrder order,
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        int maxPages = ds.getSearchResultPages(zoneId, size, maxPrice, weekDay, minRating);
        Pagination.validatePage(page, maxPages);
        List<DriverDTO> drivers = ds
                .getSearchResults(zoneId, size, maxPrice, weekDay, minRating, order, page)
                .stream().map(DriverDTO.mapper(uriInfo)).toList();
        Response.ResponseBuilder response = Response.ok(new GenericEntity<>(drivers) {
        });
        return UriUtils.addPaginationLinks(response, page, maxPages, uriInfo).build();
    }

    @POST
    @Consumes(value = {CustomMediaType.DRIVER_CREATE})
    @Produces(value = {CustomMediaType.DRIVER})
    public Response createDriver(@HeaderParam("Accept-Language") @DefaultValue("es-AR") String lang, @Valid final CreateDriverDTO dto) {
        Driver created = ds.create(dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getDescription(), dto.getCbu(), Locale.forLanguageTag(lang));
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(DriverDTO.fromDriver(uriInfo, created)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {CustomMediaType.PUBLIC_DRIVER})
    public Response getPublicDriverInfoById(@PathParam("id") int id) {
        Driver driver = ds.findById(id);
        return Response.ok(DriverDTO.fromPublicDriverInfo(uriInfo, driver)).build();
    }

    @GET
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Produces(value = {CustomMediaType.DRIVER})
    public Response getDriverById(@PathParam("id") int id) {
        Driver driver = ds.findById(id);
        return Response.ok(DriverDTO.fromDriver(uriInfo, driver)).build();
    }

    @PATCH
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes(value = {CustomMediaType.DRIVER_UPDATE})
    @Produces(value = {CustomMediaType.DRIVER})
    public Response updateDriverById(@PathParam("id") int id, @Valid final UpdateDriverDTO dto) {
        Driver d = ds.findById(id);
        ds.editProfile(
                d, dto.getUsernameOr(d.getUsername()), dto.getEmailOr(d.getMail()),
                dto.getDescriptionOr(d.getDescription()), dto.getCbuOr(d.getCbu()),
                dto.getPreferredLanguageOr(d.getLanguage().name())
        );
        if (dto.getPassword() != null && dto.getOldPassword() != null) {
            ds.updatePassword(d, dto.getPassword());
        }
        return Response.ok(DriverDTO.fromDriver(uriInfo, d)).build();
    }

    @GET
    @Path("/{id}/profile-picture")
    @Produces(value = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Response getProfilePicture(@PathParam("id") final int id) {
        Driver driver = ds.findById(id);
        Integer imageId = driver.getPfp();
        if (imageId != null) {
            Image picture = imageService.getImage(imageId);
            if (picture.getData() != null) {
                return Response.ok(picture.getData()).type(picture.getType()).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(new ImageNotFoundException())).build();
    }

    @PUT
    @Path("/{id}/profile-picture")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes(value = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Response uploadProfilePicture(@PathParam("id") final int id, @ValidImage byte[] imageData,
                                         @HeaderParam("Content-Type") String type) {
        Driver driver = ds.findById(id);
        imageService.uploadPfp(driver, imageData, "driver_" + driver.getId() + "_pfp", type);
        return Response.noContent().build();
    }

}
