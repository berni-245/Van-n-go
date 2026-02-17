package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.Pagination;

import java.util.List;

public interface MessageService {

    Message sendClientMessage(Client sender, Integer recipient, String message);


    Message sendDriverMessage(Driver sender, Integer recipient, String message);

    int getConversationMessageCount(Client client, Driver driver);

    default int getConversationPages(Client client, Driver driver) {
        return Math.max(1, (int) Math.round(Math.ceil((double) getConversationMessageCount(
                client, driver
        ) / Pagination.MAX_MESSAGE_RETRIEVAL)));
    }

    List<Message> getConversationWithoutBooking(Client client, Driver driver, int page);

    Message getMessageById(Integer messageId);
}