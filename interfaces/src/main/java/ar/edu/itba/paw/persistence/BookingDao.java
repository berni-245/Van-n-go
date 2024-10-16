package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.HourInterval;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingDao {
    Optional<Booking> appointBooking(long vehicleId, long clientId, long zoneId, LocalDate date, HourInterval hourInterval, String jobDescription);

    void acceptBooking(long bookingId);

    void rejectBooking(long bookingId);

    Optional<Booking> getBookingById(long bookingId);

    List<Booking> getDriverBookings(long driverId, int offset);

    List<Booking> getDriverHistory(long driverId, int offset);

    int getDriverBookingCount(long driverId);

    int getDriverHistoryCount(long driverId);

    List<Booking> getBookingsByVehicle(long vehicleId);

    List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date);

    List<Booking> getClientBookings(long clientId, int offset);

    int getClientBookingCount(long clientId);

    List<Booking> getClientHistory(long clientId, int offset);

    int getClientHistoryCount(long clientId);

    void setRatingAndReview(long bookingId, int rating, String review);

    List<Booking> getAllDriverBookings(long id);
}
