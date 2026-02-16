package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {

    @Mock
    private DriverDao driverDao;
    @Mock
    private ClientDao clientDao;
    @Mock
    private BookingDao bookingDao;

    @InjectMocks
    private MessageServiceImpl messageService;

    private final static Integer CLIENT_ID = 1;
    private final static Integer DRIVER_ID = 2;

    private final static String LONG_MSG = "ThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessageThisIsALongMessage";
    private final static String SHORT_MSG = "This is a short msg";

    @Test(expected = InvalidMessageException.class)
    public void testSendClientMessageMessageTooLong() {
        final Client existingClient = spy(Client.class);

        messageService.sendClientMessage(existingClient, DRIVER_ID, LONG_MSG);

        fail("Should have thrown InvalidMessageException before returning");
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testSendClientMessageForbidden() {
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);

        when(driverDao.findById(DRIVER_ID)).thenReturn(existingDriver);
        // Simulamos que no hay reservas previas entre este cliente y este conductor
        when(bookingDao.existsBookingBetween(existingClient, existingDriver)).thenReturn(false);

        messageService.sendClientMessage(existingClient, DRIVER_ID, SHORT_MSG);

        fail("Should have thrown ForbiddenConversationException");
    }

    @Test(expected = InvalidMessageException.class)
    public void testSendDriverMessageMessageTooLong() {
        final Driver existingDriver = spy(Driver.class);

        messageService.sendDriverMessage(existingDriver, CLIENT_ID, LONG_MSG);

        fail("Should have thrown InvalidMessageException");
    }

    @Test(expected = ForbiddenConversationException.class)
    public void testSendDriverMessageForbidden() {
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);

        when(clientDao.findById(CLIENT_ID)).thenReturn(existingClient);
        // Simulamos que no hay reservas previas entre este cliente y este conductor
        when(bookingDao.existsBookingBetween(existingClient, existingDriver)).thenReturn(false);

        messageService.sendDriverMessage(existingDriver, CLIENT_ID, SHORT_MSG);

        fail("Should have thrown ForbiddenConversationException");
    }
}