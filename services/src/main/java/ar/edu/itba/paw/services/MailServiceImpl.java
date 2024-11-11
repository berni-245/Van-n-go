package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.ShiftPeriod;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
    private final TemplateEngine templateEngine;
    private final ResourceBundleMessageSource messageSource;
    private final Authenticator auth;
    private final Properties mailProperties;

    @Autowired
    public MailServiceImpl(Properties mailProperties) {
        this.mailProperties = mailProperties;

        this.auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailProperties.getProperty("mail.smtp.user"),
                        mailProperties.getProperty("mail.password"));
            }
        };
        messageSource = messageSource();
        templateEngine = emailTemplateEngine();

    }


    private TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.setMessageSource(messageSource);
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(1);
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("/mail/i18n/mailMessages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }


    private void sendMail(Message message) {
        try {
            message.setSentDate(new java.util.Date());
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Error sending mail");
        }
    }

    private Message getMessage() {
        return new MimeMessage(Session.getInstance(mailProperties, auth));
    }

    private void setMailContent(Message message, String content) throws MessagingException {
        MimeBodyPart mailBody = new MimeBodyPart();
        mailBody.setContent(content, "text/html");
        Multipart multi = new MimeMultipart();
        multi.addBodyPart(mailBody);
        message.setContent(multi);
    }

    private Context getContext(Locale locale){
        Context context = new Context(locale);
        String stylesheet = mailProperties.getProperty("base.prod.url") + "css/bootstrap.min.css";
        context.setVariable("stylesheet",stylesheet);
        return context;
    }


    @Async
    @Override
    public void sendClientWelcomeMail(String to, String userName, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("userName", userName);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "login");
        String mailBodyProcessed = templateEngine.process("welcomeMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(messageSource.getMessage("subject.welcome", new Object[]{userName}, locale));
            setMailContent(message, mailBodyProcessed);
            sendMail(message);
        } catch (Exception e) {
            LOGGER.error("Error sending welcome client mail to {}", userName);
        }
        LOGGER.info("Sent welcome client mail to {}", to);
    }

    @Async
    @Override
    public void sendAcceptedBooking(LocalDate date, String driverName, String clientMail, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("date", date);
        context.setVariable("driverName", driverName);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "client/bookings");
        String mailBodyProcessed = templateEngine.process("acceptedBooking", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.bookingAccepted", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending accepted booking message to {}", clientMail);
        }
        sendMail(message);
        LOGGER.info("Sent accepted booking mail to {}", clientMail);
    }

    @Async
    @Override
    public void sendRejectedBooking(LocalDate date, String driverName, String clientMail, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("date", date);
        context.setVariable("driverName", driverName);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "client/search");
        String mailBodyProcessed = templateEngine.process("rejectedBooking", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.bookingRejected", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending rejected booking message to {}", clientMail);
        }
        sendMail(message);
        LOGGER.info("Sent rejected booking mail to {}", clientMail);
    }

    @Async
    @Override
    public void sendDriverWelcomeMail(String to, String userName, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("driverName", userName);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "driver/vehicles");
        String mailBodyProcessed = templateEngine.process("welcomeDriverMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(messageSource.getMessage("subject.welcome", new Object[]{userName}, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending welcome driver mail to {}", userName);
        }
        sendMail(message);
        LOGGER.info("Sent welcome driver mail to {}", to);
    }

    @Async
    @Override
    public void sendRequestedDriverService(String driverUsername, String driverMail, String clientUsername,
                                           String clientMail, LocalDate date, String jobDescription,
                                           String originZone, String destinationZone, ShiftPeriod period, Locale driverLocale, Locale clientLocale) {
        Map<String, Object> params = new HashMap<>();
        params.put("driverName", driverUsername);
        params.put("driverMail", driverMail);
        params.put("dateRequested", date);
        params.put("clientName", clientUsername);
        params.put("clientMail", clientMail);
        params.put("originZone", originZone);
        params.put("destinationZone", destinationZone);
        params.put("shiftPeriod", period.toString());
        params.put("jobDescription", jobDescription);
        sendClientRequestedServiceMail(clientMail, params, clientLocale);
        sendDriverRequestedMail(driverMail, params, driverLocale);
    }

    @Async
    @Override
    public void sendDriverCanceledBooking(LocalDate date, String driverUsername, String clientMail, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("date", date);
        context.setVariable("driverName", driverUsername);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "client/search");
        String mailBodyProcessed = templateEngine.process("driverCanceledBookingMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.driverCanceledBooking", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending driver canceled booking message to {}", clientMail);
        }
        sendMail(message);
        LOGGER.info("Sent client booking being canceled mail to {}", clientMail);
    }

    @Async
    @Override
    public void sendClientCanceledBooking(LocalDate date, String clientUsername, String driverMail, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("date", date);
        context.setVariable("clientName", clientUsername);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "driver/bookings?activeTab=CANCELED");
        String mailBodyProcessed = templateEngine.process("clientCanceledBookingMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(driverMail));
            message.setSubject(messageSource.getMessage("subject.clientCanceledBooking", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending client canceled booking message to {}", driverMail);
        }
        sendMail(message);
        LOGGER.info("Sent driver booking being canceled mail to {}", driverMail);
    }


    private void sendClientRequestedServiceMail(String clientMail, Map<String, Object> contextParams, Locale locale) {
        Context context = getContext(locale);
        context.setVariables(contextParams);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "client/bookings");
        Message message = getMessage();
        try {
            String mailBodyProcessed = templateEngine.process("clientRequestedServiceMail", context);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.serviceRequested", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending confirm requested service message to {}", clientMail);
        }
        sendMail(message);
        LOGGER.info("Sent client requested booking service mail to {}", clientMail);
    }

    private void sendDriverRequestedMail(String driverMail, Map<String, Object> contextParams, Locale locale) {
        Context context = getContext(locale);
        context.setVariables(contextParams);
        context.setVariable("actionUrl",mailProperties.getProperty("base.prod.url") + "driver/bookings");
        Message message = getMessage();
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(driverMail));
            String mailBodyProcessed = templateEngine.process("driverRequestedServiceMail", context);
            message.setSubject(messageSource.getMessage("subject.serviceRequired", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending requested services message to {}", driverMail);
        }
        sendMail(message);
        LOGGER.info("Sent driver received booking service mail to {}", driverMail);
    }

    @Async
    @Override
    public void sendReceivedMessage(User recipient, User sender, Booking booking, String receivedMessage, LocalDateTime timeReceived, Locale locale) {
        Message message = getMessage();
        Context context = getContext(locale);
        context.setVariable("recipientUserName", recipient.getUsername());
        context.setVariable("senderUsername", sender.getUsername());
        context.setVariable("receivedMessage", receivedMessage);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        context.setVariable("dateReceived", timeReceived.format(dateFormatter.withLocale(locale)));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        context.setVariable("timeReceived", timeReceived.format(timeFormatter));
        String chatPath = "/chat?bookingId=%d&recipientId=%d".formatted(booking.getId(), sender.getId());
        String userPath = recipient.isDriver() ? "driver" : "client";
        context.setVariable("seeChatUrl", mailProperties.getProperty("base.prod.url") + userPath + chatPath);
        String mailBodyProcessed = templateEngine.process("receivedMessageMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getMail()));
            message.setSubject(messageSource.getMessage("subject.receivedMessage", new Object[]{sender.getUsername()}, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending new message mail to {}", recipient.getUsername());
        }
        sendMail(message);
        LOGGER.info("Sent new message mail to {}", recipient.getUsername());
    }

    @Async
    @Override
    public void sendReceivedPop(String clientName, String driverMail, LocalDate date, Locale locale){
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("clientName", clientName);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        context.setVariable("date", date.format(dateFormatter.withLocale(locale)));
        context.setVariable("actionUrl", mailProperties.getProperty("base.prod.url") + "driver/bookings");
        String mailBodyProcessed = templateEngine.process("clientUploadedPop", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(driverMail));
            message.setSubject(messageSource.getMessage("subject.uploadedPop", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception e) {
            LOGGER.error("Error sending uploaded pop message to {}", driverMail);
        }
        sendMail(message);
        LOGGER.info("Sent uploaded pop mail to {}", driverMail);

    }
}
