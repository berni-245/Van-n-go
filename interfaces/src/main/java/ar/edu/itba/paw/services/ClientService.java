package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client create(String username, String mail, String password);

    Optional<Client> findById(long id);

    Optional<Booking> appointBooking(long driverId, long clientId, LocalDate date);

    default Optional<Booking> appointBooking(long driverId, long clientId, String date) {
        return appointBooking(driverId, clientId, LocalDate.parse(date));
    }

    List<Booking> getBookings(long id);

    List<Booking> getHistory(long id);
}
