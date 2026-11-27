package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Pagination;
import ar.edu.itba.paw.models.User;

import java.time.Month;
import java.util.List;

public interface UserBookingService<T extends User> {
    List<Booking> getBookings(T user, BookingState state, int page);

    List<Booking> getBookings(T user, BookingState state, Integer year, Month month);

    Booking getBookingById(T user, int id);

    boolean existsBookingBetween(int clientId, int driverId);

    int getBookingCount(T user, BookingState state);

    default int getBookingPages(T user, BookingState state) {
        int totalBookings = this.getBookingCount(user, state);
        return Math.max(1, (int) Math.round(Math.ceil((double) totalBookings / Pagination.BOOKINGS_PAGE_SIZE)));
    }

    void setNewBookingState(T user, int bookingId, BookingState newState);

    Booking cancelBooking(T user, int bookingId);
}
