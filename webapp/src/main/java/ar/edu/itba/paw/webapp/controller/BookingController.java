package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
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
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private ImageService imageService;

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

    private Booking getBookingIfAuthUserOwnsIt(int id) {
        PawUserDetails pawUserDetails = (PawUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = pawUserDetails.getUser();
        // Exceptions are handled in persistence layer
        if (user.isDriver())
            return driverService.getBookingById((Driver) user, id);
        else
            return clientService.getBookingById((Client) user, id);
    }

    @GET
    @Path("/{bookingId}")
    @Produces(value = {"application/json"})
    public Response getBookingById(@PathParam("bookingId") int bookingId) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        return Response.ok(BookingDTO.fromBooking(uriInfo, b)).build();
    }

    @GET
    @Path("/{bookingId}/proof-of-payment")
    @Produces(value = {"application/pdf"})
    public Response getBookingProofOfPayment(@PathParam("bookingId") int bookingId) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        Integer pdfId = b.getPop();
        if (pdfId == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        byte[] pdf = imageService.getImage(pdfId).getData();
        return Response.ok(pdf).build();
    }

    @POST
    @Path("/{bookingId}/proof-of-payment")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @Consumes(value = {"application/pdf"})
    @Produces(value = {"application/pdf"})
    public Response uploadBookingProofOfPayment(@PathParam("bookingId") int bookingId, InputStream inputStream) throws IOException {
        byte[] pdfData = inputStream.readAllBytes();

        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        int imgId = imageService.uploadPop(b.getClient(), pdfData, "booking_%d_pop".formatted(bookingId), bookingId);
        return Response.ok(imageService.getImage(imgId).getData()).build();
    }
}
