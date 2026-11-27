package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.Pagination;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final int MAX_MSG_LENGTH = 255;
    private final int EMAIL_THROTTLING_MINUTES = 10;

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
    public Message sendClientMessage(Client sender, Integer recipient, String message) {
        if (message.length() > MAX_MSG_LENGTH) throw new InvalidMessageException();
        Driver driver = driverDao.findById(recipient);
        if (!bookingDao.existsBookingBetween(sender, driver)) {
            throw new ForbiddenConversationException();
        }
        LocalDateTime lastMessageTime = messageDao.getLastMessageSentByUser(sender, driver, false);
        Message createdMessage = messageDao.sendMessage(sender, driver, message, false);
        if (shouldSendEmail(lastMessageTime))
            mailService.sendReceivedMessage(driver, sender, message, LocalDateTime.now(), driver.getLanguage().getLocale());
        return createdMessage;
    }

    private boolean shouldSendEmail(LocalDateTime lastMessageTime) {
        if (lastMessageTime == null) return true;
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(lastMessageTime, now).toMinutes() >= EMAIL_THROTTLING_MINUTES;
    }


    @Transactional
    @Override
    public Message sendDriverMessage(Driver sender, Integer recipient, String message) {
        if (message.length() > MAX_MSG_LENGTH) throw new InvalidMessageException();
        Client client = clientDao.findById(recipient);
        if (!bookingDao.existsBookingBetween(client, sender)) {
            throw new ForbiddenConversationException();
        }
        LocalDateTime lastMessageTime = messageDao.getLastMessageSentByUser(client, sender , true);
        Message createdMessage = messageDao.sendMessage(client, sender, message, true);
        if (shouldSendEmail(lastMessageTime)) mailService.sendReceivedMessage(client, sender, message, LocalDateTime.now(), client.getLanguage().getLocale());
        return createdMessage;
    }

    @Override
    public List<Message> getConversation(Client client, Driver driver, int page) {
        if (!bookingDao.existsBookingBetween(client, driver)) {
            throw new ForbiddenConversationException();
        }
        return messageDao.getConversation(client, driver, (page - 1) * Pagination.MAX_MESSAGE_RETRIEVAL);
    }

    @Override
    public Message getMessageById(Integer messageId) {
        return messageDao.getMessageById(messageId);
    }

    @Override
    public int getConversationMessageCount(Client client, Driver driver) {
        return messageDao.getConversationMessageCount(client, driver);
    }
}
