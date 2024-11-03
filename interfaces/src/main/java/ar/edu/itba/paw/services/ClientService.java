package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.ShiftPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface ClientService {
    Client create(String username, String mail, String password, Locale locale);

    Optional<Client> findById(long id);

    Booking appointBooking(
            long vehicleId,
            Client client,
            long zoneId,
            long destinationId,
            LocalDate date,
            ShiftPeriod shiftPeriod,
            String jobDescription,
            Locale locale
    );

    default Booking appointBooking(
            long vehicleId,
            Client client,
            long zoneId,
            long destinationId,
            String date,
            ShiftPeriod period,
            String jobDescription,
            Locale locale
    ) {
        return appointBooking(vehicleId, client, zoneId, destinationId, LocalDate.parse(date), period, jobDescription, locale);
    }

    List<Booking> getBookings(long id, int page);

    List<Booking> getHistory(long id, int page);

    long getTotalHistoryCount(long id);

    long getTotalBookingCount(long id);

    void setBookingRatingAndReview(long bookingId, int rating, String review);
}
