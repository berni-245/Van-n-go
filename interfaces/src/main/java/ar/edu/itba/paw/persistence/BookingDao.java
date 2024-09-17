package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for handling bookings.
 */
public interface BookingDao {

    /**
     * Appoints a booking for a given driver and client on a specific date.
     *
     * @param driverId The ID of the driver.
     * @param clientId The ID of the client.
     * @param date The date of the booking.
     * @return An {@link Optional} containing the appointed booking if successful, otherwise an empty {@link Optional} if the date is already appointed for that driver.
     */
    Optional<Booking> appointBooking(long driverId, long clientId, Date date);

    /**
     * Retrieves all the bookings a given driver has.
     *
     * @param driverId The ID of the driver.
     * @return A list of {@link Booking} objects for the specified driver. Will be empty if no bookings were found.
     */
    List<Booking> getBookings(long driverId);

    /**
     * Retrieves all the bookings a given driver has on a specific date.
     *
     * @param driverId The ID of the driver.
     * @param date The date of the bookings.
     * @return A list of {@link Booking} objects for the specified driver on a specific date. Will be empty if no bookings were found.
     */
    List<Booking> getBookingsByDate(long driverId, Date date);
}
