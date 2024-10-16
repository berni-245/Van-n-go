package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.Locale;

public interface MailService {
    void sendClientWelcomeMail(String to, String userName, Locale locale);

    void sendDriverWelcomeMail(String to, String userName, Locale locale);

    void sendRequestedDriverService(long driverId, long clientId, LocalDate date, String jobDescription, Locale locale);

}
