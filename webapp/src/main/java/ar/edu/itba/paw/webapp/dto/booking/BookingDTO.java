package ar.edu.itba.paw.webapp.dto.booking;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.webapp.utils.UriUtils;

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
        UriBuilder bookingTemplate = UriUtils.bookingUri(uriInfo, booking.getId());
        bookingDTO.self = bookingTemplate.clone().build();
        bookingDTO.client = UriUtils.clientUri(uriInfo, booking.getClient().getId()).build();
        bookingDTO.driver = UriUtils.driverUri(uriInfo, booking.getDriver().getId()).build();
        bookingDTO.vehicle = UriUtils.vehicleUri(uriInfo, booking.getDriver().getId(), booking.getVehicle().getId()).build();
        bookingDTO.originZone = UriUtils.zoneUri(uriInfo, booking.getOriginZone().getId()).build();
        bookingDTO.destinationZone = UriUtils.zoneUri(uriInfo, booking.getDestinationZone().getId()).build();
        bookingDTO.proofOfPayment = bookingTemplate.clone().path(UriUtils.PROOF_OF_PAYMENT).build();
        bookingDTO.review = bookingTemplate.clone().path(UriUtils.REVIEW).build();
        bookingDTO.jobDescription = booking.getJobDescription();
        bookingDTO.date = booking.getDate().toString();
        bookingDTO.shiftPeriod = booking.getShiftPeriod();
        bookingDTO.state = booking.getState();

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
