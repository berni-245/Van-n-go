package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.util.List;

public interface BookingDao {
    Booking appointBooking(
            Vehicle vehicle,
            Client client,
            Zone originZone,
            Zone destinationZone,
            LocalDate date,
            ShiftPeriod period,
            String jobDescription
    );

    void acceptBooking(Booking booking);

    void rejectBooking(Booking booking);

    void finishBooking(Booking booking);

    Booking getClientBookingById(Client client, int bookingId);

    Booking getDriverBookingById(Driver driver, int bookingId);

    List<Booking> getDriverBookings(Driver driver, BookingState state, int offset);

    int getDriverBookingCount(Driver driver, BookingState state);

    List<Booking> getClientBookings(Client client, BookingState state, int offset);

    int getClientBookingCount(Client client, BookingState state);

    void setRatingAndReview(Booking booking, int rating, String review);

    void cancelBooking(Booking booking);

    void checkPending();

    List<Booking> requestedBookingsForDate(Client client, LocalDate date, Vehicle vehicle);
}
