package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;
    private final MailService mailService;
    private final BookingDao bookingDao;

    @Autowired
    public MessageServiceImpl(MessageDao messageDao, DriverDao driverDao, ClientDao clientDao, MailService mailService, BookingDao bookingDao) {
        this.messageDao = messageDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
        this.mailService = mailService;
        this.bookingDao = bookingDao;
    }

    @Transactional
    @Override
    public void sendClientMessage(Integer bookingId, Client sender, Integer recipientId, String message) {
        sendMessage(bookingId, sender, driverDao.findById(recipientId).orElseThrow(), message, true);
    }

    @Transactional
    @Override
    public void sendDriverMessage(Integer bookingId, Driver sender, Integer recipientId, String message) {
        sendMessage(bookingId, clientDao.findById(recipientId).orElseThrow(), sender, message, false);
    }

    private void sendMessage(Integer bookingId, Client client, Driver driver, String message, boolean isClientSender) {
        if (message.length() > 255) {
            throw new InvalidMessageException();
        }

        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        checkValidConversation(booking, client, driver);

        if (isClientSender) {
            messageDao.sendMessage(client, driver, message, false);
            mailService.sendReceivedMessage(driver, client, bookingId, message, LocalDateTime.now(), driver.getLanguage().getLocale());
        } else {
            messageDao.sendMessage(client, driver, message, true);
            mailService.sendReceivedMessage(client, driver, bookingId, message, LocalDateTime.now(), client.getLanguage().getLocale());
        }
    }

    @Override
    public List<Message> getConversation(Booking booking, Client client, Driver driver) {
        checkValidConversation(booking, client, driver);
        return messageDao.getConversation(client, driver);
    }

    private void checkValidConversation(Booking booking, Client client, Driver driver) {
        if(!booking.getClient().equals(client) || !booking.getDriver().equals(driver))
            throw new ForbiddenConversationException();
    }
}
