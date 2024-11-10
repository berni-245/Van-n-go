package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import java.util.List;

public interface MessageService {
    void sendClientMessage(Integer bookingId, Client sender, Integer recipient, String message);

    void sendDriverMessage(Integer bookingId, Driver sender, Integer recipient, String message);

    List<Message> getConversation(Booking booking, Client client, Driver driver);
}