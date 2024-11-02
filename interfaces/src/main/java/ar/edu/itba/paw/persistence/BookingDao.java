package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.VehicleAlreadyAcceptedException;
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

    void acceptBooking(Booking booking) throws VehicleAlreadyAcceptedException;

    void rejectBooking(Booking booking);

    void finishBooking(Booking booking);

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
