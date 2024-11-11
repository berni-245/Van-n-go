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
    private final int MAX_MSG_LENGTH = 255;

    private final MessageDao messageDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;
    private final BookingDao bookingDao;
    private final MailService mailService;

    @Autowired
    public MessageServiceImpl(
            MessageDao messageDao, DriverDao driverDao, ClientDao clientDao,
            BookingDao bookingDao, MailService mailService) {
        this.messageDao = messageDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
        this.bookingDao = bookingDao;
        this.mailService = mailService;
    }

    @Transactional
    @Override
    public void sendClientMessage(Integer bookingId, Client sender, Integer recipientId, String message) {
        if (message.length() > MAX_MSG_LENGTH) throw new InvalidMessageException();
        Driver driver = driverDao.findById(recipientId);
        Booking booking = bookingDao.getClientBookingById(sender, bookingId);
        if (!booking.getDriver().equals(driver)) throw new ForbiddenConversationException();
        messageDao.sendMessage(sender, driver, message, false);
        mailService.sendReceivedMessage(driver, sender, booking, message, LocalDateTime.now(), driver.getLanguage().getLocale());
    }

    @Transactional
    @Override
    public void sendDriverMessage(Integer bookingId, Driver sender, Integer recipientId, String message) {
        if (message.length() > MAX_MSG_LENGTH) throw new InvalidMessageException();
        Client client = clientDao.findById(recipientId);
        Booking booking = bookingDao.getDriverBookingById(sender, bookingId);
        if (!booking.getClient().equals(client)) throw new ForbiddenConversationException();
        messageDao.sendMessage(client, sender, message, true);
        mailService.sendReceivedMessage(client, sender, booking, message, LocalDateTime.now(), client.getLanguage().getLocale());
    }

    @Override
    public List<Message> getConversation(Booking booking, Client client, Driver driver) {
        checkValidConversation(booking, client, driver);
        return messageDao.getConversation(client, driver);
    }

    private void checkValidConversation(Booking booking, Client client, Driver driver) {
        if (!booking.getClient().equals(client) || !booking.getDriver().equals(driver))
            throw new ForbiddenConversationException();
    }
}
