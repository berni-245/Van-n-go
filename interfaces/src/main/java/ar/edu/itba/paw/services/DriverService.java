package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface DriverService {
    Driver create(String username, String mail, String password, String extra1, Locale locale);

    Optional<Driver> findById(long id);

    Vehicle addVehicle(long driverId, String plateNumber, double volume, String description, double rate, String imgFilename, byte[] imgData);

    List<Vehicle> getVehicles(Driver driver);

    List<Vehicle> getVehicles(Driver driver, long zoneId, Size size);

//    List<WeeklyAvailability> getWeeklyAvailability(long id);
//
//    List<WeeklyAvailability> getWeeklyAvailability(long id, long zoneId, Size size);

    void addAvailability(
            long driverId,
            DayOfWeek[] weekDays,
            ShiftPeriod[] periods,
            long vehicleId
    );

    void updateWeeklyAvailability(
            long driverId,
            DayOfWeek weekDay,
            ShiftPeriod[] periods,
            long vehicleId
    );

    List<Driver> getAll(long zoneId, Size size, int page);

    List<Booking> getBookings(long driverId, int page);

    public List<Booking> getHistory(long driverId, int page);

    int totalMatches(long zoneId, Size size);

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

    void updateVehicle(Driver driver, long vehicleId, String plateNumber, double volume, String description, double rate, Long oldImgId, String imgFilename, byte[] imgData);
}