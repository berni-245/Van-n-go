package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
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
import java.util.Optional;
import java.util.Properties;

@Service
//@PropertySource("classpath:resources/mail/mailConfig.properties") //TODO: pasar las configuraciones a mailConfig.properties
public class MailServiceImpl implements MailService {

    @Autowired
    private final DriverDao driverDao;
    @Autowired
    private final ClientDao clientDao;


    private final TemplateEngine templateEngine;

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
        templateEngine = emailTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
    }


    private TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.setMessageSource(messageSource());
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
        messageSource.setBasename("/i18n/messages");  // Ruta base a tus archivos de propiedades
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true); // Muestra el código si no se encuentra la clave
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
        } catch (MessagingException ignored) {
            //logger
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
    public void sendClientWelcomeMail(String to, String userName) {
        Message message = getMessage();
        Context context = new Context();
        context.setVariable("userName", userName);
        String mailBodyProcessed = templateEngine.process("welcomeMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Welcome " + userName);
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {
            //logger
        }


        sendMail(message);
    }

    @Async
    @Override
    public void sendHaulerWelcomeMail(String to, String userName) {
        Message message = getMessage();
        Context context = new Context();
        context.setVariable("haulerName", userName);
        String mailBodyProcessed = templateEngine.process("welcomeDriverMail", context);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Welcome " + userName);
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {
            //logger
        }

        sendMail(message);
    }

    @Async
    @Override
    public void sendRequestedDriverService(long driverId, long clientId, LocalDate date, String jobDescription) {
        Optional<Driver> driver = driverDao.findById(driverId);
        Optional<Client> client = clientDao.findById(clientId);
        if(driver.isPresent() && client.isPresent()){
            Context context = new Context();
            String clientMail = client.get().getMail();
            String driverMail = driver.get().getMail();
            context.setVariable("haulerName", driver.get().getUsername());
            context.setVariable("haulerMail", driver.get().getMail());
            context.setVariable("dateRequested", new java.util.Date());
            context.setVariable("clientName", driverMail);
            context.setVariable("clientMail", clientMail );
            sendClientRequestedServiceMail(clientMail, context);
            sendHaulerRequestedMail(driverMail, context, jobDescription);

        }

    }


    private void sendClientRequestedServiceMail(String clientMail, Context context) {
        Message message = getMessage();
        try {
            String mailBodyProcessed = templateEngine.process("clientRequestedServiceMail", context);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clientMail));
            message.setSubject("You requested a Van N' Go hauler");
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignored) {
        }


        sendMail(message);
    }

    private void sendHaulerRequestedMail(String haulerMail, Context context, String jobDescription) {
        context.setVariable("jobDescription", jobDescription);
        Message message = getMessage();
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(haulerMail));
            String mailBodyProcessed = templateEngine.process("haulerRequestedServiceMail", context);
            message.setSubject("You received a request for your service");
            setMailContent(message, mailBodyProcessed);
        } catch (Exception ignore) {
        }

        sendMail(message);
    }


}
