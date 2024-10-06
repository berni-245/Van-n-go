package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.HourInterval;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingDao {

    Optional<Booking> appointBooking(long vehicleId, long clientId, LocalDate date, HourInterval hourInterval);

    void acceptBooking(long bookingId);

    void rejectBooking(long bookingId);

    public Optional<Booking> getBookingById(long bookingId);

    List<Booking> getBookings(long vehicleId);

    List<Booking> getBookingsByDate(long vehicleId, LocalDate date);

    List<Booking> getClientBookings(long clientId);

    List<Booking> getClientHistory(long clientId);

    void setRating(long bookingId, int rating);
}
