package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.webapp.dto.BookingDTO;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/bookings")
@Component
public class BookingController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DriverService driverService;

    @GET
    @Produces(value = {"application/json"})
    public Response getBookings(
            @QueryParam("fromClient") Integer fromClientId,
            @QueryParam("toDriver") Integer toDriverId,
            @QueryParam("state") BookingState state,
            @QueryParam("page") @DefaultValue("1") int page
    ) {

        if ((fromClientId == null && toDriverId == null) || (fromClientId != null && toDriverId != null)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO()).build();
        }
        List<BookingDTO> bookings;
        if (fromClientId != null) {
            Client client = clientService.findById(fromClientId);
            bookings = clientService.getBookings(client, state, page).stream().map(BookingDTO.mapper(uriInfo)).toList();
            return Response.ok(new GenericEntity<>(bookings) {
            }).build();
        }
        if (toDriverId != null) {
            Driver driver = driverService.findById(toDriverId);
            bookings = driverService.getBookings(driver, state, page).stream().map(BookingDTO.mapper(uriInfo)).toList();
            return Response.ok(new GenericEntity<>(bookings) {
            }).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO()).build();
    }


}
