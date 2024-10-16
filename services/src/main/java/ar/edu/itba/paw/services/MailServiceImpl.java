package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
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

    @Autowired
    private final DriverDao driverDao;
    @Autowired
    private final ClientDao clientDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
    private final TemplateEngine templateEngine;
    private final ResourceBundleMessageSource messageSource;

    private final String FROM = "van.n.go.paw@gmail.com";
    private final String password = "lrbe ukez jvkk fleu";
    private final Authenticator auth;
    private final Properties properties;

    public MailServiceImpl(DriverDao driverDao, ClientDao clientDao) {
        this.driverDao = driverDao;
        this.clientDao = clientDao;
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
            message.setSubject(messageSource.getMessage("subject.welcome",new Object[]{userName},locale));
            setMailContent(message, mailBodyProcessed);
            sendMail(message);
        } catch (Exception e) {
            LOGGER.error("Error sending welcome client mail to {}", userName);
        }
    }

    @Async
    @Override
    public void sendAcceptedBooking(long bookingId, Locale locale){
        Booking booking = null; //= bookingDao.getBooking(id);
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("date", booking.getDate());
        context.setVariable("driverName",booking.getDriver().getUsername());
        String mailBodyProcessed = templateEngine.process("acceptedBooking", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(booking.getClient().getMail()));
            message.setSubject(messageSource.getMessage("subject.bookingAccepted",null,locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {

        }
        sendMail(message);
    }

    @Async
    @Override
    public void sendRejectedBooking(long bookingId, Locale locale){
        Booking booking = null ;//bookingDao.getBooking(id);
        Message message = getMessage();
        Context context = new Context(locale);
        context.setVariable("date", booking.getDate());
        context.setVariable("driverName",booking.getDriver().getUsername());
        String mailBodyProcessed = templateEngine.process("rejectedBooking", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(booking.getClient().getMail()));
            message.setSubject(messageSource.getMessage("subject.bookingRejected",null,locale));
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
            message.setSubject(messageSource.getMessage("subject.welcome",new Object[]{userName},locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {
            LOGGER.error("Error sending welcome driver mail to {}", userName);
        }
        sendMail(message);
    }

    @Async
    @Override
    public void sendRequestedDriverService(long driverId, long clientId, LocalDate date, String jobDescription, Locale locale) {
        Optional<Driver> driver = driverDao.findById(driverId);
        Optional<Client> client = clientDao.findById(clientId);
        if(driver.isPresent() && client.isPresent()){
            Context context = new Context(locale);
            String clientMail = client.get().getMail();
            String driverMail = driver.get().getMail();
            context.setVariable("driverName", driver.get().getUsername());
            context.setVariable("driverMail", driver.get().getMail());
            context.setVariable("dateRequested", new java.util.Date());
            context.setVariable("clientName", driverMail);
            context.setVariable("clientMail", clientMail );
            sendClientRequestedServiceMail(clientMail, context, locale);
            sendDriverRequestedMail(driverMail, context, jobDescription, locale);

        }

    }


    private void sendClientRequestedServiceMail(String clientMail, Context context,Locale locale) {
        Message message = getMessage();
        try {
            String mailBodyProcessed = templateEngine.process("clientRequestedServiceMail", context);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject(messageSource.getMessage("subject.serviceRequired",null,locale));
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
            message.setSubject(messageSource.getMessage("subject.serviceRequested",null,locale));
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignore) {
        }
        sendMail(message);
    }
}
