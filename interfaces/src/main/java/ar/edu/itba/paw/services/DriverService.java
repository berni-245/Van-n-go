package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;

public interface DriverService extends UserService<Driver> {
    Driver create(String username, String mail, String password, String description, String cbu, Locale locale);

    Vehicle addVehicle(
            Driver driver,
            String plateNumber,
            double volume,
            String description,
            double rate
    );

    List<Vehicle> getVehicles(Driver driver, Integer zoneId, Size size, Double priceMax, DayOfWeek weekday, int page, boolean mustHaveAvailability);

    // TODO the 3 update methods prolly need a test, the availability one apparently it didn't have a test?
    void updateAvailability(
            Vehicle vehicle,
            List<Integer> zoneIds,
            Map<DayOfWeek, ShiftPeriod[]> periods
    );

    List<Driver> getSearchResults(
            int zoneId, Size size, Double priceMax,
            DayOfWeek weekday, Integer rating, SearchOrder order, int page
    );

    int getSearchResultCount(
            int zoneId, Size size,
            Double priceMax, DayOfWeek weekday, Integer rating
    );

    default int getSearchResultPages(
            int zoneId, Size size, Double priceMax,
            DayOfWeek weekday, Integer rating
    ) {
        return Math.max(1, (int) Math.round(Math.ceil((double) getSearchResultCount(
                zoneId, size, priceMax, weekday, rating
        ) / Pagination.SEARCH_PAGE_SIZE)));
    }

    void acceptBooking(Driver driver, int bookingId);

    void rejectBooking(Driver driver, int bookingId);

    Optional<Vehicle> findVehicleById(Driver driver, int vehicleId);

    Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber);

    void editProfile(Driver driver, String username, String mail, String description, String cbu, String language);

    Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles);

    void updateVehicle(Driver driver, Integer vehicleId, String plateNumber, double volume, String description, double rate);

    void updatePicture(Vehicle vehicle, String imgFilename, byte[] imgData, String type);

    void finishBooking(Driver driver, int bookingId);

    int getVehicleCount(Driver driver, Integer zoneId, Size size, Double priceMax, DayOfWeek weekday, boolean mustHaveAvailability);

    default int getVehiclePages(Driver driver, Integer zoneId, Size size, Double priceMax, DayOfWeek weekday, boolean mustHaveAvailability) {
        return Math.max(1, (int) Math.round(Math.ceil((double) getVehicleCount(driver, zoneId, size, priceMax, weekday, mustHaveAvailability) / Pagination.VEHICLES_PAGE_SIZE)));
    }

    void deleteVehicle(Vehicle vehicle);

    List<Booking> getBookingsForVehicleInYearMonth(Vehicle vehicle, Integer year, Month month);
}