package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public interface DriverService {
    Driver create(String username, String mail, String password, String extra1, Locale locale);

    Optional<Driver> findById(long id);

    Vehicle addVehicle(
            long driverId,
            String plateNumber,
            double volume,
            String description,
            List<Long> zoneIds,
            double rate,
            String imgFilename,
            byte[] imgData
    );

    List<Vehicle> getVehicles(Driver driver);

    List<Vehicle> getVehicles(Driver driver, long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday);

    void updateAvailability(
            Vehicle vehicle,
            ShiftPeriod[] mondayPeriods,
            ShiftPeriod[] tuesdayPeriods,
            ShiftPeriod[] wednesdayPeriods,
            ShiftPeriod[] thursdayPeriods,
            ShiftPeriod[] fridayPeriods,
            ShiftPeriod[] saturdayPeriods,
            ShiftPeriod[] sundayPeriods
    );

    List<Driver> getAll(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, int page);

    List<Booking> getBookings(Driver driver, BookingState state, int page);

    long getBookingCount(Driver driver, BookingState state);

    default int getBookingPages(Driver driver, BookingState state) {
        long totalBookings = this.getBookingCount(driver, state);
        return (int) Math.ceil((double) totalBookings / Pagination.BOOKINGS_PAGE_SIZE);
    }

    int totalMatches(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating);

    List<Booking> getBookingsByVehicle(long vehicleId);

    List<Booking> getBookingsByVehicle(long vehicleId, LocalDate date);

    default List<Booking> getBookingsByVehicle(long vehicleId, String date) {
        return getBookingsByVehicle(vehicleId, LocalDate.parse(date));
    }

    void acceptBooking(long bookingId, Locale locale);

    void rejectBooking(long bookingId, Locale locale);

    Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber);

    void editProfile(Driver driver, String username, String mail, String extra1, String cbu);

    Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles);

    void updateVehicle(Driver driver, long vehicleId, String plateNumber, double volume, String description, List<Long> zoneIds, double rate, Long oldImgId, String imgFilename, byte[] imgData);

    void finishBooking(long bookingId);

    List<Vehicle> getVehicles(Driver driver, int page);

    long getVehicleCount(Driver driver);

    void deleteVehicle(Vehicle vehicle);

    void updatePassword(long id, String password);
}