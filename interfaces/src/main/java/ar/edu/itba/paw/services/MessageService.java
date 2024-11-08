package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import java.util.List;

public interface MessageService {
    void sendClientMessage(Client sender, Driver recipient, String message);

    void sendDriverMessage(Driver sender, Client recipient, String message);

    List<Message> getConversation(Driver driver, Client client);
}