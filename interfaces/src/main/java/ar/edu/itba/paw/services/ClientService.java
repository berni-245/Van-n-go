package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.ShiftPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ClientService extends UserBookingService<Client> {
    Client create(String username, String mail, String password, Locale locale);

    Client findById(long id);

    Booking appointBooking(
            long vehicleId,
            Client client,
            long zoneId,
            long destinationId,
            LocalDate date,
            ShiftPeriod shiftPeriod,
            String jobDescription
    );

    default Booking appointBooking(
            long vehicleId,
            Client client,
            long zoneId,
            long destinationId,
            String date,
            ShiftPeriod period,
            String jobDescription
    ) {
        return appointBooking(vehicleId, client, zoneId, destinationId, LocalDate.parse(date), period, jobDescription);
    }

    void setBookingRatingAndReview(long bookingId, int rating, String review);

    void updatePassword(long id, String password);

    void editProfile(Client client, String username, String mail, Long zoneId, String language);

    void cancelBooking(long bookingId, Client client);
}
