package ar.edu.itba.paw.models;

import java.time.LocalDate;

public class Booking {
    private final long bookingId;
    private final Client user;
    private final Driver driver;
    private final LocalDate date;
    private final boolean confirmed;
    private final Integer rating;
    private final String review;
    private final Integer pop;

    public Booking(long bookingId, Client user, LocalDate date, boolean confirmed, Driver driver, Integer rating,String review, Integer pop) {
        this.bookingId = bookingId;
        this.user = user;
        this.driver = driver;
        this.date = date;
        this.confirmed = confirmed;
        this.rating = rating;
        this.review = review;
        this.pop = pop;
    }



    public long getBookingId() {
        return bookingId;
    }

    public Client getClient() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public Driver getDriver() {return driver;}

    public Integer getRating() {return rating;}

    public String getReview() {return review;}

    public Integer getPop() {
        return pop;
    }


}
