package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class BookingReviewForm {

    @Min(0)
    @Max(5)
    private int rating;

    @Length(min = 6, max = 255)
    private String review;

    private  long bookingID;


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public @Length(min = 6, max = 255) String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public long getBookingID() {
        return bookingID;
    }

    public void setBookingID(long bookingID) {
        this.bookingID = bookingID;
    }
}
