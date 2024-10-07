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

    Optional<Booking> appointBooking(long vehicleId, long clientId, long zoneId, LocalDate date, HourInterval hourInterval, String jobDescription);

    default Optional<Booking> appointBooking(long vehicleId, long clientId, long zoneId, String date, HourInterval hourInterval, String jobDescription) {
        return appointBooking(vehicleId, clientId, zoneId, LocalDate.parse(date), hourInterval, jobDescription);
    }

    List<Booking> getBookings(long id);

    List<Booking> getHistory(long id);

    void setBookingRatingAndReview(long bookingId, int rating, String review);
}
