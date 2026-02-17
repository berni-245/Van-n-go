package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.dto.booking.*;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import ar.edu.itba.paw.webapp.validation.ValidFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @Autowired
    private ImageService imageService;

    @GET
    @PreAuthorize("@authService.isAuthUser(#userId)")
    @Produces(value = {CustomMediaType.BOOKING_LIST})
    public <T extends User> Response listBookings(
            @QueryParam("userId") Integer userId,
            @QueryParam("state") BookingState state,
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        T user = getAuthUser();
        UserService<T> userService = getBookingServiceFor(user);
        int maxPages = userService.getBookingPages(user, state);
        Pagination.validatePage(page, maxPages);
        List<BookingDTO> bookings = userService.getBookings(user, state, page).stream().map(BookingDTO.mapper(uriInfo)).toList();
        Response.ResponseBuilder response = Response.ok(new GenericEntity<>(bookings) {
        });
        return UriUtils.addPaginationLinks(response, page, maxPages, uriInfo).build();
    }

    @POST
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @Consumes(value = {CustomMediaType.BOOKING_CREATE})
    @Produces(value = {CustomMediaType.BOOKING})
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

    @GET
    @Path("/{bookingId}")
    @Produces(value = {CustomMediaType.BOOKING})
    public Response getBookingById(@PathParam("bookingId") int bookingId) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        return Response.ok(BookingDTO.fromBooking(uriInfo, b)).build();
    }

    @POST
    @Path("/{bookingId}")
    @Consumes(value = {CustomMediaType.BOOKING_UPDATE_STATE})
    @Produces(value = {CustomMediaType.BOOKING})
    public <T extends User> Response updateBookingState(@PathParam("bookingId") int bookingId, UpdateBookingStateDTO dto) {
        T user = getAuthUser();
        UserService<T> userService = getBookingServiceFor(user);
        Booking b = userService.getBookingById(user, bookingId);
        userService.setNewBookingState(user, b.getId(), BookingState.valueOf(dto.getState().name()));
        return Response.ok(BookingDTO.fromBooking(uriInfo, b)).build();
    }

    @GET
    @Path("/{bookingId}/review")
    @Produces(value = {CustomMediaType.BOOKING_REVIEW})
    public Response getReview(@PathParam("bookingId") int bookingId) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        if (b.getRating() == null)
            Response.status(Response.Status.NOT_FOUND);
        return Response.ok(BookingReviewDTO.fromBooking(uriInfo, b)).build();
    }

    @POST
    @Path("/{bookingId}/review")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @Consumes(value = {CustomMediaType.BOOKING_REVIEW})
    @Produces(value = {CustomMediaType.BOOKING_REVIEW})
    public Response updateBookingReview(@PathParam("bookingId") int bookingId, CreateBookingReviewDTO dto) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        clientService.setBookingRatingAndReview(b.getClient(), b.getId(), dto.getRating(), dto.getComments());
        return Response.ok(BookingReviewDTO.fromBooking(uriInfo, b)).build();
    }

    @GET
    @Path("/{bookingId}/proof-of-payment")
    @Produces(value = {MediaType.APPLICATION_PDF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Response getBookingProofOfPayment(@PathParam("bookingId") int bookingId) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        Integer popId = b.getPop();
        if (popId == null)
            return Response.status(Response.Status.NOT_FOUND).type(CustomMediaType.ERROR)
                    .entity(new ErrorDTO(new ImageNotFoundException())).build();
        Image pop = imageService.getImage(popId);
        return Response.ok(pop.getData()).type(pop.getType()).build();
    }

    @PUT
    @Path("/{bookingId}/proof-of-payment")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @Consumes(value = {MediaType.APPLICATION_PDF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @Produces(value = {MediaType.APPLICATION_PDF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Response uploadBookingProofOfPayment(@PathParam("bookingId") int bookingId, @ValidFile byte[] proofOfPayment,
                                                @HeaderParam("Content-Type") String contentType) {
        Booking b = getBookingIfAuthUserOwnsIt(bookingId);
        imageService.uploadPop(b.getClient(), proofOfPayment, "booking_%d_pop".formatted(bookingId), bookingId, contentType);
        return Response.noContent().build();
    }

    private <T extends User> Booking getBookingIfAuthUserOwnsIt(int bookingId) {
        T user = getAuthUser();
        UserService<T> userService = getBookingServiceFor(user);
        // Exceptions are handled in persistence layer
        return userService.getBookingById(user, bookingId);
    }

    @SuppressWarnings("unchecked")
    private <T extends User> T getAuthUser() {
        PawUserDetails pawUserDetails = (PawUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = pawUserDetails.getUser();
        return (T) user;
    }

    @SuppressWarnings("unchecked")
    private <T extends User> UserService<T> getBookingServiceFor(T user) {
        if (user.isDriver())
            return (UserService<T>) driverService;
        else
            return (UserService<T>) clientService;
    }

}
