package ar.edu.itba.paw.models;

import java.util.Date;

public class Booking {
    private final long bookingId;
    private final long clientId;
    private final Date date;


    public Booking(long bookingId, long clientId, Date date) {
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.date = date;
    }

    public long getBookingId() {
        return bookingId;
    }

    public long getClientId() {
        return clientId;
    }

    public Date getDate() {
        return date;
    }
}
