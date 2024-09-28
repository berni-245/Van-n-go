package ar.edu.itba.paw.models;

import java.time.LocalDate;

public class Booking {
    private final long bookingId;
    private final Client user;
    private final Driver driver;
    private final LocalDate date;
    private final boolean confirmed;

    public Booking(long bookingId, Client user, LocalDate date, boolean confirmed, Driver driver) {
        this.bookingId = bookingId;
        this.user = user;
        this.driver = driver;
        this.date = date;
        this.confirmed = confirmed;
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

    public Driver getDriver() {
        return driver;
    }
}
