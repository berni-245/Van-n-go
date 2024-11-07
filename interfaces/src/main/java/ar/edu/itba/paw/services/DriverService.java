package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public interface DriverService extends UserBookingService<Driver> {
    Driver create(String username, String mail, String password, String description, Locale locale);

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

    List<Driver> getSearchResults(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, SearchOrder order, int page);

    int totalMatches(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating);

    List<Booking> getBookingsByVehicle(long vehicleId);

    List<Booking> getBookingsByVehicle(long vehicleId, LocalDate date);

    default List<Booking> getBookingsByVehicle(long vehicleId, String date) {
        return getBookingsByVehicle(vehicleId, LocalDate.parse(date));
    }

    void acceptBooking(long bookingId,Driver driver, Locale locale);

    void rejectBooking(long bookingId,Driver driver, Locale locale);

    Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber);

    void editProfile(Driver driver, String username, String mail, String description, String cbu);

    Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles);

    void updateVehicle(Driver driver, long vehicleId, String plateNumber, double volume, String description, List<Long> zoneIds, double rate, Long oldImgId, String imgFilename, byte[] imgData);

    void finishBooking(long bookingId, Driver driver, Locale locale);

    List<Vehicle> getVehicles(Driver driver, int page);

    long getVehicleCount(Driver driver);

    void deleteVehicle(Vehicle vehicle);

    void updatePassword(long id, String password);

    void cancelBooking(long bookingId, Driver driver, Locale locale);
}