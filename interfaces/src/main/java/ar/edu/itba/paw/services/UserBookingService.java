package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface UserBookingService<T extends User> {
    List<Booking> getBookings(T user, BookingState state, int page);

    long getBookingCount(T user, BookingState state);

    default int getBookingPages(T user, BookingState state) {
        long totalBookings = this.getBookingCount(user, state);
        return (int) Math.ceil((double) totalBookings / Pagination.BOOKINGS_PAGE_SIZE);
    }

    Booking cancelBooking(long bookingId, T user);
}
