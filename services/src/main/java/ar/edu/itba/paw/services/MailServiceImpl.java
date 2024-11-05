package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
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
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

@Service
//@PropertySource("classpath:resources/mail/mailConfig.properties") //TODO: pasar las configuraciones a mailConfig.properties
public class MailServiceImpl implements MailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
    private final TemplateEngine templateEngine;
    private final ResourceBundleMessageSource messageSource;

    private final String FROM = "van.n.go.paw@gmail.com";
    private final String password = "lrbe ukez jvkk fleu";
    private final Authenticator auth;
    private final Properties properties;

    public MailServiceImpl() {
        this.properties = new Properties();
        setProperties();

        this.auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, password);
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

    private void setProperties() {
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.user", FROM);
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.auth", "true");
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
        return new MimeMessage(Session.getInstance(properties, auth));
    }

    private void setMailContent(Message message, String content) throws MessagingException {
        MimeBodyPart mailBody = new MimeBodyPart();
        mailBody.setContent(content, "text/html");
        Multipart multi = new MimeMultipart();
        multi.addBodyPart(mailBody);
        message.setContent(multi);
    }


    @Async
    @Override
    public void sendClientWelcomeMail(String to, String userName, Locale locale) {
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("userName", userName);
        String mailBodyProcessed = templateEngine.process("welcomeMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(messageSource.getMessage("subject.welcome", new Object[]{userName}, locale));
            setMailContent(message, mailBodyProcessed);
            sendMail(message);
        } catch (Exception e) {
            LOGGER.error("Error sending welcome client mail to {}", userName);
        }
    }

    @Async
    @Override
    public void sendAcceptedBooking(LocalDate date, String driverName, String clientMail, Locale locale) {
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("date", date);
        context.setVariable("driverName", driverName);
        String mailBodyProcessed = templateEngine.process("acceptedBooking", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.bookingAccepted", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {

        }
        sendMail(message);
    }

    @Async
    @Override
    public void sendRejectedBooking(LocalDate date, String driverName, String clientMail, Locale locale) {
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("date", date);
        context.setVariable("driverName", driverName);
        String mailBodyProcessed = templateEngine.process("rejectedBooking", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.bookingRejected", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {

        }
        sendMail(message);
    }

    @Async
    @Override
    public void sendDriverWelcomeMail(String to, String userName, Locale locale) {
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("driverName", userName);
        String mailBodyProcessed = templateEngine.process("welcomeDriverMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(messageSource.getMessage("subject.welcome", new Object[]{userName}, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {
            LOGGER.error("Error sending welcome driver mail to {}", userName);
        }
        sendMail(message);
    }

    @Async
    @Override
    public void sendRequestedDriverService(String driverUsername, String driverMail, String clientUsername,
                                           String clientMail, LocalDate date, String jobDescription,
                                           String originZone, String destinationZone, ShiftPeriod period, Locale locale) {
        Context context = new Context(locale);
        context.setVariable("driverName", driverUsername);
        context.setVariable("driverMail", driverMail);
        context.setVariable("dateRequested", date);
        context.setVariable("clientName", clientUsername);
        context.setVariable("clientMail", clientMail);
        context.setVariable("originZone", originZone);
        context.setVariable("destinationZone", destinationZone);
        sendClientRequestedServiceMail(clientMail, context, jobDescription, locale);
        sendDriverRequestedMail(driverMail, context, jobDescription, locale);
    }

    @Override
    public void sendDriverCanceledBooking(LocalDate date, String driverUsername, String clientMail, Locale locale) {
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("date", date);
        context.setVariable("driverName", driverUsername);
        String mailBodyProcessed = templateEngine.process("driverCanceledBookingMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.driverCanceledBooking", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {

        }
        sendMail(message);
    }

    @Override
    public void sendClientCanceledBooking(LocalDate date, String clientUsername, String driverMail, Locale locale) {
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("date", date);
        context.setVariable("clientName", clientUsername);
        String mailBodyProcessed = templateEngine.process("clientCanceledBookingMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(driverMail));
            message.setSubject(messageSource.getMessage("subject.driverCanceledBooking", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {

        }
        sendMail(message);
    }


    private void sendClientRequestedServiceMail(String clientMail, Context context, String jobDescription, Locale locale) {
        context.setVariable("jobDescription", jobDescription);
        Message message = getMessage();
        try {
            String mailBodyProcessed = templateEngine.process("clientRequestedServiceMail", context);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.serviceRequested", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {
        }
        sendMail(message);
    }

    private void sendDriverRequestedMail(String driverMail, Context context, String jobDescription, Locale locale) {
        context.setVariable("jobDescription", jobDescription);
        Message message = getMessage();
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(driverMail));
            String mailBodyProcessed = templateEngine.process("driverRequestedServiceMail", context);
            message.setSubject(messageSource.getMessage("subject.serviceRequired", null, locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignore) {
        }
        sendMail(message);
    }
}
