package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public interface DriverService extends UserService<Driver> {
    Driver create(String username, String mail, String password, String description, Locale locale);

    Vehicle addVehicle(
            Driver driver,
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

    List<Driver> getSearchResults(
            long zoneId, Size size, Double priceMin, Double priceMax,
            DayOfWeek weekday, Integer rating, SearchOrder order, int page
    );

    int getSearchResultCount(
            long zoneId, Size size, Double priceMin,
            Double priceMax, DayOfWeek weekday, Integer rating
    );

    default int getSearchResultPages(
            long zoneId, Size size, Double priceMin,
            Double priceMax, DayOfWeek weekday, Integer rating
    ) {
        return (int) Math.ceil((double) getSearchResultCount(
                zoneId, size, priceMin, priceMax, weekday, rating
        ) / Pagination.SEARCH_PAGE_SIZE);
    }

    List<Booking> getBookingsByVehicle(long vehicleId);

    List<Booking> getBookingsByVehicle(long vehicleId, LocalDate date);

    default List<Booking> getBookingsByVehicle(long vehicleId, String date) {
        return getBookingsByVehicle(vehicleId, LocalDate.parse(date));
    }

    void acceptBooking(long bookingId, Driver driver);

    void rejectBooking(long bookingId, Driver driver);

    Optional<Vehicle> findVehicleById(Driver driver, long vehicleId);

    Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber);

    void editProfile(Driver driver, String username, String mail, String description, String cbu);

    Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles);

    void updateVehicle(Driver driver, long vehicleId, String plateNumber, double volume, String description, List<Long> zoneIds, double rate, Long oldImgId, String imgFilename, byte[] imgData);

    void finishBooking(long bookingId, Driver driver);

    List<Vehicle> getVehicles(Driver driver, int page);

    long getVehicleCount(Driver driver);

    void deleteVehicle(Vehicle vehicle);
}