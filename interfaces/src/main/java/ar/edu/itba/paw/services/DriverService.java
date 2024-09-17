package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DriverService {
    Driver create(String username, String mail, String password, String extra1);

    Optional<Driver> findById(long id);

    Vehicle addVehicle(long driverId, String plateNumber, double volume, String description);

    List<Vehicle> getVehicles(long id);

    List<WeeklyAvailability> getWeeklyAvailability(long id);

    List<WeeklyAvailability> addWeeklyAvailability(
            long driverId,
            int[] weekDays,
            String timeStart,
            String timeEnd,
            long[] zoneIds,
            long[] vehicleIds
    );

    List<Driver> getAll();

    List<Driver> getAll(long zoneId);

    List<Driver> getAll(long zoneId, Size size);

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
    List<Booking> getBookingsByDate(long driverId, LocalDate date);

    /**
     * Accepts a booking for a given driver and the booking id. Rejects all the other bookings on that day for that driver.
     *
     * @param bookingId The ID of the booking.
     */
    void acceptBooking(long bookingId);

    /**
     * Rejects a booking for a given booking id. Removing it from the Dao.
     *
     * @param bookingId The ID of the booking.
     */
    void rejectBooking(long bookingId);
}