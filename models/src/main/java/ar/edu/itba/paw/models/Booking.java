package ar.edu.itba.paw.models;

import java.util.Date;

public class Booking {
    private final long bookingId;
    private final User user;
    private final Date date;
    private final boolean confirmed;


    public Booking(long bookingId, User user, Date date, boolean confirmed) {
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

    public Date getDate() {
        return date;
    }

    public boolean getConfirmed() {
        return confirmed;
    }
}
