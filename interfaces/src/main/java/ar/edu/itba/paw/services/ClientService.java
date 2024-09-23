package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client create(String username, String mail, String password);

    Optional<Client> findById(long id);

    /**
     * Appoints a booking for a given driver and client on a specific date.
     *
     * @param driverId The ID of the driver.
     * @param clientId The ID of the client.
     * @param date The date of the booking.
     * @return An {@link Optional} containing the appointed booking if successful, otherwise an empty {@link Optional} if the date is already appointed for that driver.
     */
    Optional<Booking> appointBooking(long driverId, long clientId, LocalDate date);

    /**
     * Appoints a booking for a given driver and client on a specific date.
     *
     * @param driverId The ID of the driver.
     * @param clientId The ID of the client.
     * @param date The date of the booking.
     * @return An {@link Optional} containing the appointed booking if successful, otherwise an empty {@link Optional} if the date is already appointed for that driver.
     */
    default Optional<Booking> appointBooking(long driverId, long clientId, String date) {
        return appointBooking(driverId, clientId, LocalDate.parse(date));
    }

    /**
     * Appoints a booking for a given driver and client on a specific date.
     *
     * @param driverUsername The username of the driver.
     * @param clientUsername The username of the client.
     * @param date The date of the booking.
     * @return An {@link Optional} containing the appointed booking if successful, otherwise an empty {@link Optional} if the date is already appointed for that driver.
     */
    Optional<Booking> appointBooking(String driverUsername, String clientUsername, LocalDate date);

    /**
     * Appoints a booking for a given driver and client on a specific date.
     *
     * @param driverUsername The username of the driver.
     * @param clientUsername The username of the client.
     * @param date The date of the booking.
     * @return An {@link Optional} containing the appointed booking if successful, otherwise an empty {@link Optional} if the date is already appointed for that driver.
     */
    default Optional<Booking> appointBooking(String driverUsername, String clientUsername, String date) {
        return appointBooking(driverUsername, clientUsername, LocalDate.parse(date));
    }

    List<Booking> getBookings(long id);
}
