package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BookingReviewForm {

    @Min(1)
    @Max(5)
    private int rating;

    @Length(min = 6, max = 255)
    private String review;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}
