package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface BookingDao {
    Booking appointBooking(
            Vehicle vehicle,
            Client client,
            Zone originZone,
            Zone destinationZone,
            LocalDate date,
            ShiftPeriod period,
            String jobDescription
    );

    void acceptBooking(Booking booking);

    void rejectBooking(Booking booking);

    void finishBooking(Booking booking);

    Booking getClientBookingById(Client client, int bookingId);

    Booking getDriverBookingById(Driver driver, int bookingId);

    List<Booking> getDriverBookings(Driver driver, BookingState state, int offset);

    List<Booking> getDriverBookings(Driver driver, BookingState state, YearMonth yearMonth);

    int getDriverBookingCount(Driver driver, BookingState state);

    List<Booking> getClientBookings(Client client, BookingState state, int offset);

    List<Booking> getClientBookings(Client client, BookingState state, YearMonth yearMonth);

    int getClientBookingCount(Client client, BookingState state);

    void setRatingAndReview(Booking booking, int rating, String review);

    void cancelBooking(Booking booking);

    void rejectOldPendingBookings();

    boolean existsBookingBetween(Client client, Driver driver);

    boolean existsBookingBetween(int clientId, int driverId);

    boolean hasAnAcceptedBooking(Vehicle vehicle);

    void rejectAllPendingBookingsFromVehicle(Vehicle vehicle);

    List<Booking> getBookingsByVehicleInYearMonth(Vehicle vehicle, YearMonth yearMonth);
}
