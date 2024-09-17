package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.util.Date;

public class Booking {
    private final long bookingId;
    private final User user;
    private final LocalDate date;
    private final boolean confirmed;


    public Booking(long bookingId, User user, LocalDate date, boolean confirmed) {
        this.bookingId = bookingId;
        this.user = user;
        this.date = date;
        this.confirmed = confirmed;
    }

    public long getBookingId() {
        return bookingId;
    }

    public User getClient() {
        return user;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean getConfirmed() {
        return confirmed;
    }
}
