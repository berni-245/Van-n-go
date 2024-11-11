package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import org.springframework.scheduling.annotation.Async;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public interface MailService {
    void sendClientWelcomeMail(String to, String userName, Locale locale);


    void sendAcceptedBooking(LocalDate date, String driverName, String clientMail, Locale locale);


    void sendRejectedBooking(LocalDate date, String driverName, String clientMail, Locale locale);

    void sendDriverWelcomeMail(String to, String userName, Locale locale);

    void sendRequestedDriverService(String driverUsername, String driverMail, String clientUsername,
                                    String clientMail, LocalDate date, String jobDescription,
                                    String originZone, String destinationZone, ShiftPeriod period, Locale driverlocale, Locale clientLocal);

    void sendDriverCanceledBooking(LocalDate date, String username, String mail, Locale locale);

    void sendClientCanceledBooking(LocalDate date, String username, String mail, Locale locale);


    void sendReceivedMessage(User recipient, User sender, Booking booking, String receivedMessage, LocalDateTime timeReceived, Locale locale);


    void sendReceivedPop(String clientName, String driverMail, LocalDate date, Locale locale);
}
