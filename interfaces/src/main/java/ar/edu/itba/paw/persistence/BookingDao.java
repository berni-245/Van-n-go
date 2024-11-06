package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    Optional<Booking> getBookingById(long bookingId);

    List<Booking> getDriverBookings(Driver driver, BookingState state, int offset);

    long getDriverBookingCount(Driver driver, BookingState state);

    List<Booking> getBookingsByVehicle(Vehicle vehicle);

    List<Booking> getBookingsByVehicle(Vehicle vehicle, LocalDate date);

    List<Booking> getClientBookings(Client client, BookingState state, int offset);

    long getClientBookingCount(Client client, BookingState state);

    void setRatingAndReview(Booking booking, int rating, String review);

    void cancelBooking(Booking booking);
}
