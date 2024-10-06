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

    void addWeeklyAvailability(
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

    List<Booking> getBookingsByVehicle(long vehicleId);

    List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date);

    default List<Booking> getBookingsByVehicleAndDate(long vehicleId, String date) {
        return getBookingsByVehicleAndDate(vehicleId, LocalDate.parse(date));
    }

    void acceptBooking(long bookingId);

    void rejectBooking(long bookingId);

    Optional<Vehicle> findVehicleByPlateNumber(long driverId, String plateNumber);

    boolean updateVehicle(long driverId, Vehicle vehicle);
}