package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ClientService {
    Client create(String username, String mail, String password, Locale locale);

    Optional<Client> findById(long id);

    Optional<Booking> appointBooking(long driverId, long clientId, LocalDate date,String jobDescription, Locale locale);

    List<Booking> getBookings(long id);

    List<Booking> getHistory(long id);

    void setBookingRatingAndReview(long bookingId, int rating, String review);
}
