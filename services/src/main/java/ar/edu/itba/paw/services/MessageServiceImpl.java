package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.persistence.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;

    @Autowired
    public MessageServiceImpl(final MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Transactional
    @Override
    public void sendClientMessage(Client sender, Driver recipient, String message) {
        if(message.length()>255)
            throw new InvalidMessageException();
        messageDao.sendMessage(sender,recipient,message,false);
    }

    @Transactional
    @Override
    public void sendDriverMessage(Driver sender, Client recipient, String message) {
        if(message.length()>255)
            throw new InvalidMessageException();
        messageDao.sendMessage(recipient,sender,message,true);
    }

    @Override
    public List<Message> getConversation(Driver driver, Client client) {
        return messageDao.getConversation(client,driver);
    }
}
