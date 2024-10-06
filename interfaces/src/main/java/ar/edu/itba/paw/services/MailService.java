package ar.edu.itba.paw.services;

import java.time.LocalDate;

public interface MailService {
    void sendClientWelcomeMail(String to, String userName);

    void sendDriverWelcomeMail(String to, String userName);

    void sendRequestedDriverService(long driverId, long clientId, LocalDate date, String jobDescription);

}
