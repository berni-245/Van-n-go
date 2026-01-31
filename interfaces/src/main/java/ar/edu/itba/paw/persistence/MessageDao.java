package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;

import java.util.List;

public interface MessageDao {
    List<Message> getConversation(Client client, Driver driver);

    Message sendMessage(Client client, Driver driver, String content, boolean sentByDriver);

    Message getMessageById(Integer messageId);
}
