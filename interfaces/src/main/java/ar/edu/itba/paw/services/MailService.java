package ar.edu.itba.paw.services;

public interface MailService {
    boolean sendWelcomeMail(String to,String userName);
    void sendRequestedHauler(String clientMail, String haulerMail, String clientName, String haulerName,String jobDescription);
}
