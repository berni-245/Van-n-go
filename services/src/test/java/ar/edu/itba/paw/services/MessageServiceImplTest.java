package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Vehicle;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.MessageDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {
    @Mock
    private MessageDao messageDao;
    @Mock
    private DriverDao driverDao;
    @Mock
    private ClientDao clientDao;
    @Mock
    private BookingDao bookingDao;
    @Mock
    private MailService mailService;
    @InjectMocks
    private MessageServiceImpl messageService;

    private final static Integer BOOKING_ID = 1;
    private final static Integer CLIENT_ID = 1;
    private final static Integer DRIVER_ID = 2;
    private final static Integer CLIENT_ID_TWO = 2;
    private final static Integer DRIVER_ID_TWO = 2;

    private final static String LONG_MSG = "ThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessage";
    private final static String SHORT_MSG = "This is a short msg";

    @Test(expected = InvalidMessageException.class)
    public void testSendClientMessageMessageTooLong() {
        final Client existingClient = spy(Client.class);
        messageService.sendClientMessage(BOOKING_ID, existingClient, DRIVER_ID, LONG_MSG);

        fail();
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testSendClientMessageForbidden() {
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        final Driver existingDriverTwo = spy(Driver.class);
        final Booking existingBooking = spy(Booking.class);
        final Vehicle existingVehicle = spy(Vehicle.class);

        existingBooking.setVehicle(existingVehicle);
        when(existingBooking.getDriver()).thenReturn(existingDriverTwo);

        when(driverDao.findById(DRIVER_ID)).thenReturn(existingDriver);
        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);

        // Since the driver in the booking is different from the once send the message, forbidden
        messageService.sendClientMessage(BOOKING_ID, existingClient, DRIVER_ID, SHORT_MSG);

        fail();
    }

    @Test(expected = InvalidMessageException.class)
    public void testSendDriverMessageMessageTooLong() {
        final Driver existingDriver = spy(Driver.class);
        messageService.sendDriverMessage(BOOKING_ID, existingDriver, CLIENT_ID, LONG_MSG);

        fail();
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testSendDriverMessageForbidden() {
        final Client existingClient = spy(Client.class);
        final Client existingClientTwo = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);

        final Booking existingBooking = spy(Booking.class);
        when(existingBooking.getClient()).thenReturn(existingClientTwo);

        when(clientDao.findById(CLIENT_ID)).thenReturn(existingClient);
        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        // Since the client in the booking is different from the once send the message, forbidden
        messageService.sendDriverMessage(BOOKING_ID, existingDriver, CLIENT_ID, SHORT_MSG);

        fail();
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testGetConversationWrongClient() {
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        final Client existingClientTwo = spy(Client.class);
        final Booking existingBooking = spy(Booking.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setVehicle(existingVehicle);

        when(existingBooking.getClient()).thenReturn(existingClientTwo);
        when(existingBooking.getDriver()).thenReturn(existingDriver);


        // in booking = existingClientTwo
        // in request = existingClient
        // exception since they are different
        messageService.getConversation(existingBooking, existingClient, existingDriver);

        fail();
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testGetConversationWrongDriver() {
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        final Driver existingDriverTwo = spy(Driver.class);
        final Booking existingBooking = spy(Booking.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setVehicle(existingVehicle);

        when(existingBooking.getClient()).thenReturn(existingClient);
        when(existingBooking.getDriver()).thenReturn(existingDriverTwo);

        // in booking = existingDriverTwo
        // in request = existingDriver
        // exception since they are different
        messageService.getConversation(existingBooking, existingClient, existingDriver);

        fail();
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testGetConversationWrongDriverAndClient() {
        final Client existingClient = spy(Client.class);
        final Client existingClientTwo = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        final Driver existingDriverTwo = spy(Driver.class);
        final Booking existingBooking = spy(Booking.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setVehicle(existingVehicle);

        when(existingBooking.getClient()).thenReturn(existingClientTwo);
        when(existingBooking.getDriver()).thenReturn(existingDriverTwo);

        // in booking = existingDriverTwo and existingClientTwo
        // in request = existingDriver and existingClient
        // exception since they are different
        messageService.getConversation(existingBooking, existingClient, existingDriver);

        fail();
    }

    // There's not too much point in testing successful sent message because I can't really see the updated conversation
    // Sending message will be tested in persistence
}
