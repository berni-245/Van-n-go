package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.util.Optional;

public class Booking {
    private final long bookingId;
    private final Client client;
    private final Driver driver;
    private final Vehicle vehicle;
    private final LocalDate date;
    private final HourInterval hourInterval;
    private final BookingState bookingState;
    private final Image proofOfPayment;
    private final Integer rating;
    private final String review;
    private final boolean confirmed;

    public Booking(long bookingId, Client client, Driver driver, Vehicle vehicle,
                   LocalDate date, HourInterval hourInterval, BookingState bookingState,
                   Image proofOfPayment, Integer rating, String review) {
        this.bookingId = bookingId;
        this.client = client;
        this.driver = driver;
        this.vehicle = vehicle;
        this.date = date;
        this.hourInterval = hourInterval;
        this.bookingState = bookingState;
        this.proofOfPayment = proofOfPayment;
        this.rating = rating;
        this.review = review;
        this.confirmed = bookingState.equals(BookingState.ACCEPTED);
    }

    public long getBookingId() {
        return bookingId;
    }

    public Driver getDriver() {
        return driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getDate() {
        return date;
    }

    public HourInterval getHourInterval() {
        return hourInterval;
    }

    public BookingState getBookingState() {
        return bookingState;
    }

    public Client getClient() {
        return client;
    }

    public Optional<Image> getProofOfPayment() {
        return Optional.ofNullable(proofOfPayment);
    }

    public Optional<Integer> getRating() {
        return Optional.ofNullable(rating);
    }

    public Optional<String> getReview() {
        return Optional.ofNullable(review);
    }

    public boolean getConfirmed() {
        return confirmed;
    }
}
