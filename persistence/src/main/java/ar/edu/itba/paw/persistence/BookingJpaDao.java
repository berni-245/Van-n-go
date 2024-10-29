package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingJpaDao implements BookingDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Booking appointBooking(
            Vehicle vehicle,
            Client client,
            Zone originZone,
            LocalDate date,
            ShiftPeriod period,
            String jobDescription
    ) {
        throw new UnsupportedOperationException("Not supported yet.");

        // Check if booking is valid.

        // Booking booking = new Booking(
        //         null, client, driver, vehicle,
        //         originZone, date, period,
        //         BookingState.PENDING, null,
        //         null, null, jobDescription
        // );
        // em.persist(booking);
        // return booking;
    }

    // Creo que había que hacerlo transactional para que se persistan los cambios
    // hechos a la entity
    @Transactional
    @Override
    public void acceptBooking(Booking booking) {
        booking.setState(BookingState.ACCEPTED);
    }

    @Override
    public void rejectBooking(long bookingId) {

    }

    @Override
    public Optional<Booking> getBookingById(long bookingId) {
        return Optional.empty();
    }

    @Override
    public List<Booking> getDriverBookings(long driverId, int offset) {
        return List.of();
    }

    @Override
    public List<Booking> getDriverHistory(long driverId, int offset) {
        return List.of();
    }

    @Override
    public int getDriverBookingCount(long driverId) {
        return 0;
    }

    @Override
    public int getDriverHistoryCount(long driverId) {
        return 0;
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        return List.of();
    }

    @Override
    public List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date) {
        return List.of();
    }

    @Override
    public List<Booking> getClientBookings(long clientId, int offset) {
        return List.of();
    }

    @Override
    public int getClientBookingCount(long clientId) {
        return 0;
    }

    @Override
    public List<Booking> getClientHistory(long clientId, int offset) {
        return List.of();
    }

    @Override
    public int getClientHistoryCount(long clientId) {
        return 0;
    }

    @Override
    public void setRatingAndReview(long bookingId, int rating, String review) {

    }

    @Override
    public List<Booking> getAllDriverBookings(long id) {
        return List.of();
    }
}
