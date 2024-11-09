package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import java.util.List;

public interface MessageService {
    void sendClientMessage(Client sender, Integer recipient, String message);

    void sendDriverMessage(Driver sender, Integer recipient, String message);

    List<Message> getConversation(Client client, Driver driver);
}