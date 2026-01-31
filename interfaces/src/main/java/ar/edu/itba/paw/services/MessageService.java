package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;

import java.util.List;

public interface MessageService {
    void sendClientMessage(Integer bookingId, Client sender, Integer recipient, String message);

    Message sendClientMessageWithoutBooking(Client sender, Integer recipient, String message);

    void sendDriverMessage(Integer bookingId, Driver sender, Integer recipient, String message);

    Message sendDriverMessageWithoutBooking(Driver sender, Integer recipient, String message); //TODO: hacer que los tests usen los metdos nuevos que no dependen del booking

    List<Message> getConversation(Booking booking, Client client, Driver driver);

    List<Message> getConversationWithoutBooking(Client client, Driver driver);

    Message getMessageById(Integer messageId);
}