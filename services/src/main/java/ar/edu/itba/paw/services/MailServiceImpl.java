package ar.edu.itba.paw.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Service
//@PropertySource("classpath:resources/mail/mailConfig.properties") //TODO: pasar las configuraciones a mailConfig.properties
public class MailServiceImpl implements MailService {

    @Autowired
    private TemplateEngine templateEngine;

    private final String FROM = "van.n.go.paw@gmail.com";
    private final String password = "lrbe ukez jvkk fleu";
    private final Authenticator auth;
    private final Properties properties;

    MailServiceImpl() {
        this.properties = new Properties();
        setProperties();

        this.auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, password);
            }
        };

    }

    private void setProperties() {
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.user", FROM);
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.auth", "true");
    }

    private boolean sendMail(Message message) {
        try {
            message.setSentDate(new java.util.Date());
            Transport.send(message);
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }

    private Message getMessage() {
        return new MimeMessage(Session.getInstance(properties, auth));
    }

    private void setMailContent(Message message, String content) throws MessagingException {
        MimeBodyPart mailBody = new MimeBodyPart();
        mailBody.setContent(content, "text/html");
        Multipart multi = new MimeMultipart();
        multi.addBodyPart(mailBody);
        message.setContent(multi);
    }


    @Override
    public boolean sendWelcomeMail(String to, String userName) {
        Message message = getMessage();
        Context context = new Context();
        String mailBodyProcessed = templateEngine.process("welcomeMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Welcome " + userName);
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            return false;
        }


        return sendMail(message);
    }

    @Override
    public void sendRequestedHauler(String clientMail, String haulerMail, String clientName, String haulerName, String jobDescription) {
        Context context = new Context();
        context.setVariable("haulerName", haulerName);
        context.setVariable("haulerMail", haulerMail);
        context.setVariable("dateRequested", new java.util.Date());
        context.setVariable("clientName", clientName);
        context.setVariable("clientMail", clientMail);
        sendClientRequestedServiceMail(clientMail, context);
        sendHaulerRequestedMail(haulerMail, context, jobDescription);
    }

    private boolean sendClientRequestedServiceMail(String clientMail, Context context) {
        Message message = getMessage();
        try {
            String mailBodyProcessed = templateEngine.process("clientRequestedServiceMail", context);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject("You requested a Van N' Go hauler");
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            return false;
        }


        return sendMail(message);
    }

    private boolean sendHaulerRequestedMail(String haulerMail, Context context, String jobDescription) {
        context.setVariable("jobDescription", jobDescription);
        Message message = getMessage();
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(haulerMail));
            String mailBodyProcessed = templateEngine.process("haulerRequestedServiceMail", context);
            message.setSubject("You received a request for your service");
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            return false;
        }
        return sendMail(message);
    }


}
