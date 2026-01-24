package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.ShiftPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public interface ClientService extends UserService<Client>, UserBookingService<Client> {
    Client create(String username, String mail, String password, int zoneId, Locale locale);

    Booking appointBooking(
            int vehicleId,
            Client client,
            int zoneId,
            int destinationId,
            LocalDate date,
            ShiftPeriod shiftPeriod,
            String jobDescription
    );

    void setBookingRatingAndReview(Client user, int bookingId, int rating, String review);

    void editProfile(Client client, String username, String mail, Integer zoneId, String language);

    List<ShiftPeriod> requestedShiftPeriodsForDate(Client client, LocalDate date, String plateNumber);
}
