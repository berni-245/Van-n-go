package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.Pagination;

import java.util.List;

public interface MessageService {
    void sendClientMessage(Integer bookingId, Client sender, Integer recipient, String message);

    Message sendClientMessageWithoutBooking(Client sender, Integer recipient, String message);

    void sendDriverMessage(Integer bookingId, Driver sender, Integer recipient, String message);

    Message sendDriverMessageWithoutBooking(Driver sender, Integer recipient, String message); //TODO: hacer que los tests usen los metdos nuevos que no dependen del booking

    int getConversationMessageCount(Client client, Driver driver);

    default int getConversationPages(Client client, Driver driver) {
        return (int) Math.round(Math.ceil((double) getConversationMessageCount(
                client, driver
        ) / Pagination.MAX_MESSAGE_RETRIEVAL));
    }

    List<Message> getConversationWithoutBooking(Client client, Driver driver, int page);

    Message getMessageById(Integer messageId);
}