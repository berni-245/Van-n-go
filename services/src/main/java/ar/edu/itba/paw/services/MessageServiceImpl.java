package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.exceptions.InvalidRecipientException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;


@Service
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;
    private final MailService mailService;

    @Autowired
    public MessageServiceImpl(MessageDao messageDao, DriverDao driverDao, ClientDao clientDao, MailService mailService) {
        this.messageDao = messageDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
        this.mailService = mailService;
    }

    @Transactional
    @Override
    public void sendClientMessage(Client sender, Integer recipientId, String message) {
        sendMessage(sender, recipientId, message, true);
    }

    @Transactional
    @Override
    public void sendDriverMessage(Driver sender, Integer recipientId, String message) {
        sendMessage(sender, recipientId, message, false);
    }

    private void sendMessage(Object sender, Integer recipientId, String message, boolean isClientSender) {
        if (message.length() > 255) {
            throw new InvalidMessageException();
        }

        if (isClientSender) {
            Driver recipient = driverDao.findById(recipientId)
                    .orElseThrow(InvalidRecipientException::new);
            messageDao.sendMessage((Client) sender, recipient, message, false);
            mailService.sendReceivedMessage(recipient.getUsername(), recipient.getMail(), ((Client) sender).getUsername(), ((Client) sender).getId(), true, message, LocalDateTime.now(), recipient.getLanguage().getLocale());
        } else {
            Client recipient = clientDao.findById(recipientId)
                    .orElseThrow(InvalidRecipientException::new);
            messageDao.sendMessage(recipient, (Driver) sender, message, true);
            mailService.sendReceivedMessage(recipient.getUsername(), recipient.getMail(), ((Driver) sender).getUsername(), ((Driver) sender).getId(), false, message, LocalDateTime.now(), recipient.getLanguage().getLocale());
        }
    }

    @Override
    public List<Message> getConversation(Client client, Driver driver) {
        return messageDao.getConversation(client, driver);
    }
}
