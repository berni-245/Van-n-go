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
import java.util.List;


@Service
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;
    private final DriverDao driverDao;
    private final ClientDao clientDao;

    @Autowired
    public MessageServiceImpl(MessageDao messageDao, DriverDao driverDao, ClientDao clientDao) {
        this.messageDao = messageDao;
        this.driverDao = driverDao;
        this.clientDao = clientDao;
    }

    @Transactional
    @Override
    public void sendClientMessage(Client sender, Long recipientId, String message) {
        sendMessage(sender, recipientId, message, true);
    }

    @Transactional
    @Override
    public void sendDriverMessage(Driver sender, Long recipientId, String message) {
        sendMessage(sender, recipientId, message, false);
    }

    private void sendMessage(Object sender, Long recipientId, String message, boolean isClientSender) {
        if (message.length() > 255) {
            throw new InvalidMessageException();
        }

        if (isClientSender) {
            Driver recipient = driverDao.findById(recipientId)
                    .orElseThrow(InvalidRecipientException::new);
            messageDao.sendMessage((Client) sender, recipient, message, false);
        } else {
            Client recipient = clientDao.findById(recipientId)
                    .orElseThrow(InvalidRecipientException::new);
            messageDao.sendMessage(recipient, (Driver) sender, message, true);
        }
    }

    @Override
    public List<Message> getConversation(Client client, Driver driver) {
        return messageDao.getConversation(client, driver);
    }
}
