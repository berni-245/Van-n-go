package ar.edu.itba.paw.services;

public interface MailService {
    void sendClientWelcomeMail(String to,String userName);
    void sendHaulerWelcomeMail(String to,String userName,long id);
    void sendRequestedHauler(String clientMail, String haulerMail, String clientName, String haulerName,String jobDescription);

}
