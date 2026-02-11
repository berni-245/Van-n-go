package ar.edu.itba.paw.webapp.dto.booking;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateBookingReviewDTO {
    @Min(1)
    @Max(5)
    @NotNull
    private Integer rating;

    @Length(max = 255)
    private String comments;

    public CreateBookingReviewDTO() {
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
}
