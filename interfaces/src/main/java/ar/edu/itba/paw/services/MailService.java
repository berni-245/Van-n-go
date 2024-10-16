package ar.edu.itba.paw.services;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.Locale;

public interface MailService {
    void sendClientWelcomeMail(String to, String userName, Locale locale);

    @Async
    void sendAcceptedBooking(long bookingId, Locale locale);

    @Async
    void sendRejectedBooking(long bookingId, Locale locale);

    void sendDriverWelcomeMail(String to, String userName, Locale locale);

    void sendRequestedDriverService(long driverId, long clientId, LocalDate date, String jobDescription, Locale locale);

}
