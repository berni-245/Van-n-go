package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DriverService {
    Driver create(String username, String mail, String password, String extra1);

    Optional<Driver> findById(long id);

    Vehicle addVehicle(long driverId, String plateNumber, double volume, String description, double rate);

    List<Vehicle> getVehicles(long id);

    List<Vehicle> getVehiclesFull(long id);

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

    List<Booking> getBookings(long driverId);

    List<Booking> getBookingsByDate(long driverId, LocalDate date);

    default List<Booking> getBookingsByDate(long driverId, String date) {
        return getBookingsByDate(driverId, LocalDate.parse(date));
    }

    /**
     * Accepts a booking for a given driver and the booking id. Rejects all the other bookings on that day for that driver.
     *
     * @param bookingId The ID of the booking.
     */
    void acceptBooking(long bookingId);

    void rejectBooking(long bookingId);

    public Optional<Vehicle> findVehicleByPlateNumber(long driverId, String plateNumber);

    public boolean updateVehicle(long driverId, Vehicle vehicle);
}