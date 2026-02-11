package ar.edu.itba.paw.webapp.dto.booking;

import ar.edu.itba.paw.models.Booking;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class BookingReviewDTO {
    private Integer rating;
    private String comments;
    private URI booking;

    public static BookingReviewDTO fromBooking(UriInfo uriInfo, Booking booking) {
        BookingReviewDTO dto = new BookingReviewDTO();
        dto.rating = booking.getRating();
        dto.comments = booking.getReview();
        dto.booking = uriInfo.getBaseUriBuilder().path("api").path("bookings").path(String.valueOf(booking.getId())).build();
        return dto;
    }

    public BookingReviewDTO() {
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public URI getBooking() {
        return booking;
    }

    public void setBooking(URI booking) {
        this.booking = booking;
    }
}
