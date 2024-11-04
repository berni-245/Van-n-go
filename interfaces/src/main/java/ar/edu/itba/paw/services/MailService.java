package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import org.springframework.scheduling.annotation.Async;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.Locale;

public interface MailService {
    void sendClientWelcomeMail(String to, String userName, Locale locale);


    void sendAcceptedBooking(LocalDate date, String driverName, String clientMail, Locale locale);


    void sendRejectedBooking(LocalDate date, String driverName, String clientMail, Locale locale);

    void sendDriverWelcomeMail(String to, String userName, Locale locale);

    void sendRequestedDriverService(long driverId, Client client, LocalDate date, String jobDescription, Locale locale);

}
