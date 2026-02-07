package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.dto.booking.BookingDTO;
import ar.edu.itba.paw.webapp.dto.booking.CreateBookingDTO;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
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
    @PreAuthorize("@authService.isAuthUserEither(#fromClientId, #toDriverId)")
    @Produces(value = {"application/json"})
    public Response listBookings(
            @QueryParam("fromClient") Integer fromClientId,
            @QueryParam("toDriver") Integer toDriverId,
            @QueryParam("state") BookingState state,
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        if ((fromClientId == null) == (toDriverId == null)) { // Basically a not xor
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO()).build();
        }

        List<BookingDTO> bookings;
        if (fromClientId != null) {
            Client client = clientService.findById(fromClientId);
            bookings = clientService.getBookings(client, state, page).stream().map(BookingDTO.mapper(uriInfo)).toList();
            return Response.ok(new GenericEntity<>(bookings) {
            }).build();
        }

        Driver driver = driverService.findById(toDriverId);
        bookings = driverService.getBookings(driver, state, page).stream().map(BookingDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(bookings) {
        }).build();
    }

    @POST
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @Consumes(value = {"application/json"})
    @Produces(value = {"application/json"})
    public Response createBooking(@Valid final CreateBookingDTO createBookingDTO) {
        PawUserDetails pawUserDetails = (PawUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Client client = (Client) pawUserDetails.getUser();
        Booking booking = clientService.appointBooking(createBookingDTO.getVehicleId(),
                client,
                createBookingDTO.getOriginZoneId(),
                createBookingDTO.getDestinationZoneId(),
                createBookingDTO.getDate(),
                createBookingDTO.getShiftPeriod(),
                createBookingDTO.getJobDescription());
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(booking.getId())).build();
        return Response.created(location).entity(BookingDTO.fromBooking(uriInfo, booking)).build();
    }

    public Response getBookingById() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    public Response getBookingProofOfPayment() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    public Response uploadBookingProofOfPayment() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
