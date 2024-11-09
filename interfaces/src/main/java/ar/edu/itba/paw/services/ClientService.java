package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.ShiftPeriod;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

public interface ClientService extends UserService<Client>, UserBookingService<Client> {
    Client create(String username, String mail, String password, Locale locale);

    Booking appointBooking(
            long vehicleId,
            Client client,
            long zoneId,
            long destinationId,
            LocalDate date,
            ShiftPeriod shiftPeriod,
            String jobDescription
    );

    void setBookingRatingAndReview(long bookingId, int rating, String review);

    void editProfile(Client client, String username, String mail, Long zoneId, String language);
}
