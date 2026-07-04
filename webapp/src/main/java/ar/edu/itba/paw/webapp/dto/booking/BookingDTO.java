package ar.edu.itba.paw.webapp.dto.booking;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.ShiftPeriod;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class BookingDTO {

    private int id;
    private URI self;
    private URI client;
    private URI driver;
    private URI vehicle;
    private URI originZone;
    private URI destinationZone;
    private URI proofOfPayment;
    private boolean hasProofOfPayment;
    private String jobDescription;
    private String date;
    private ShiftPeriod shiftPeriod;
    private BookingState state;
    private URI review;

    public static Function<Booking, BookingDTO> mapper(UriInfo uriInfo) {
        return (booking) -> fromBooking(uriInfo, booking);
    }

    public static BookingDTO fromBooking(UriInfo uriInfo, Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.id = booking.getId();
        UriBuilder bookingTemplate = uriInfo.getBaseUriBuilder().path("api").path("bookings").path(String.valueOf(booking.getId()));
        bookingDTO.self = bookingTemplate.clone().build();
        bookingDTO.client = uriInfo.getBaseUriBuilder().path("api").path("clients").path(String.valueOf(booking.getClient().getId())).build();
        UriBuilder driverTemplate = uriInfo.getBaseUriBuilder().path("api").path("drivers").path(String.valueOf(booking.getDriver().getId()));
        bookingDTO.driver = driverTemplate.clone().build();
        bookingDTO.vehicle = driverTemplate.clone().path("vehicles").path(String.valueOf(booking.getVehicle().getId())).build();
        bookingDTO.originZone = uriInfo.getBaseUriBuilder().path("api").path("zones").path(String.valueOf(booking.getOriginZone().getId())).build();
        bookingDTO.proofOfPayment = bookingTemplate.clone().path("proof-of-payment").build();
        bookingDTO.hasProofOfPayment = booking.getPop() != null;
        bookingDTO.review = booking.getReview() != null ? bookingTemplate.clone().path("review").build() : null;
        bookingDTO.jobDescription = booking.getJobDescription();
        bookingDTO.date = booking.getDate().toString();
        bookingDTO.shiftPeriod = booking.getShiftPeriod();
        bookingDTO.state = booking.getState();

        if (booking.getDestinationZone() != null) {
            bookingDTO.destinationZone = uriInfo.getBaseUriBuilder().path("api").path("zones").path(String.valueOf(booking.getDestinationZone().getId())).build();
        }
        return bookingDTO;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getClient() {
        return client;
    }

    public void setClient(URI client) {
        this.client = client;
    }

    public URI getDriver() {
        return driver;
    }

    public void setDriver(URI driver) {
        this.driver = driver;
    }

    public URI getVehicle() {
        return vehicle;
    }

    public void setVehicle(URI vehicle) {
        this.vehicle = vehicle;
    }

    public URI getOriginZone() {
        return originZone;
    }

    public void setOriginZone(URI originZone) {
        this.originZone = originZone;
    }

    public URI getDestinationZone() {
        return destinationZone;
    }

    public void setDestinationZone(URI destinationZone) {
        this.destinationZone = destinationZone;
    }

    public URI getProofOfPayment() {
        return proofOfPayment;
    }

    public void setProofOfPayment(URI proofOfPayment) {
        this.proofOfPayment = proofOfPayment;
    }

    public boolean getHasProofOfPayment() {
        return hasProofOfPayment;
    }

    public void setHasProofOfPayment(boolean hasProofOfPayment) {
        this.hasProofOfPayment = hasProofOfPayment;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ShiftPeriod getShiftPeriod() {
        return shiftPeriod;
    }

    public void setShiftPeriod(ShiftPeriod shiftPeriod) {
        this.shiftPeriod = shiftPeriod;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public URI getReview() {
        return review;
    }

    public void setReview(URI review) {
        this.review = review;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
