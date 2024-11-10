package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
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
            List<Integer> zoneIds,
            double rate,
            String imgFilename,
            byte[] imgData
    );

    List<Vehicle> getVehicles(Driver driver, int zoneId, Size size, Double priceMax, DayOfWeek weekday);

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
        return (int) Math.ceil((double) getSearchResultCount(
                zoneId, size, priceMax, weekday, rating
        ) / Pagination.SEARCH_PAGE_SIZE);
    }

    void acceptBooking(int bookingId, Driver driver);

    void rejectBooking(int bookingId, Driver driver);

    Optional<Vehicle> findVehicleById(Driver driver, int vehicleId);

    Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber);

    void editProfile(Driver driver, String username, String mail, String description, String cbu, String language);

    Set<DayOfWeek> getWorkingDays(List<Vehicle> vehicles);

    void updateVehicle(Driver driver, int vehicleId, String plateNumber, double volume, String description, List<Integer> zoneIds, double rate, Integer oldImgId, String imgFilename, byte[] imgData);

    void finishBooking(int bookingId, Driver driver);

    List<Vehicle> getVehicles(Driver driver, int page);

    int getVehicleCount(Driver driver);

    void deleteVehicle(Vehicle vehicle);
}