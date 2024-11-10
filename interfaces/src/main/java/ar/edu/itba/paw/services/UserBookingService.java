package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface UserBookingService<T extends User> {
    List<Booking> getBookings(T user, BookingState state, int page);

    Optional<Booking> getBookingById(int bookingId);

    int getBookingCount(T user, BookingState state);

    default int getBookingPages(T user, BookingState state) {
        int totalBookings = this.getBookingCount(user, state);
        return (int) Math.ceil((double) totalBookings / Pagination.BOOKINGS_PAGE_SIZE);
    }

    Booking cancelBooking(int bookingId, T user);
}
