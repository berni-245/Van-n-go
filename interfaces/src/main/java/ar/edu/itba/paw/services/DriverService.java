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

    List<Vehicle> getVehicles(Driver driver, long zoneId, Size size);

//    List<WeeklyAvailability> getWeeklyAvailability(long id);
//
//    List<WeeklyAvailability> getWeeklyAvailability(long id, long zoneId, Size size);

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

    void updateWeeklyAvailability(
            long driverId,
            DayOfWeek weekDay,
            ShiftPeriod[] periods,
            long vehicleId
    );

    List<Driver> getAll(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating, int page);

    List<Booking> getBookings(long driverId, int page);

    public List<Booking> getHistory(long driverId, int page);

    int totalMatches(long zoneId, Size size, Double priceMin, Double priceMax, DayOfWeek weekday, Integer rating);

    int getTotalBookingCount(long driverId);

    int getTotalHistoryCount(long driverId);

    List<Booking> getBookingsByVehicle(long vehicleId);

    List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date);

    default List<Booking> getBookingsByVehicleAndDate(long vehicleId, String date) {
        return getBookingsByVehicleAndDate(vehicleId, LocalDate.parse(date));
    }

    void acceptBooking(long bookingId);

    void rejectBooking(long bookingId);

    Optional<Vehicle> findVehicleByPlateNumber(Driver driver, String plateNumber);

    void editProfile(long id, String extra1, String cbu);

//    List<WeeklyAvailability>  activeAvailabilities(long vehicleId, long zoneId, LocalDate date);

    List<Booking> getAllBookings(long id);

    Set<DayOfWeek> getDriverWorkingDaysOnZoneWithSize(Driver driver, long zoneId, Size size);

    void updateVehicle(Driver driver, long vehicleId, String plateNumber, double volume, String description, List<Long> zoneIds, double rate, Long oldImgId, String imgFilename, byte[] imgData);

    void finishBooking(long bookingId);

    List<Vehicle> getVehicles(Driver driver, int page);

    int getVehicleCount(Driver driver);

    void deleteVehicle(Vehicle vehicle);
}