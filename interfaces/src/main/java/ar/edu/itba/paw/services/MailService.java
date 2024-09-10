package ar.edu.itba.paw.services;

import java.util.Date;

public interface MailService {
    boolean sendWelcomeMail(String to,String userName);
    boolean sendRequestedHauler(String clientMail, String haulerMail, String clientName, String haulerName, Date requestDate);
}
