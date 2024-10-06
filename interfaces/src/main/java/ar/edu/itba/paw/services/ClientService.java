package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.HourInterval;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client create(String username, String mail, String password);

    Optional<Client> findById(long id);

    Optional<Booking> appointBooking(long vehicleId, long clientId, LocalDate date, HourInterval hourInterval);

    default Optional<Booking> appointBooking(long vehicleId, long clientId, String date, HourInterval hourInterval) {
        return appointBooking(vehicleId, clientId, LocalDate.parse(date), hourInterval);
    }

    List<Booking> getBookings(long id);

    List<Booking> getHistory(long id);

    void setBookingRatingAndReview(long bookingId, int rating, String review);
}
