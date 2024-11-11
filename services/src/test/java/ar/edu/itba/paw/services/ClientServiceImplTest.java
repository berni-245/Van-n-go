package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenClientCancelBookingException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceImplTest {

    @Mock
    private UserDao<Client> userDao;
    @Mock
    private ClientDao clientDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private BookingDao bookingDao;
    @Mock
    private VehicleDao vehicleDao;
    @Mock
    private ZoneDao zoneDao;

    @InjectMocks
    private ClientServiceImpl clientService;

    private static final Integer BOOKING_ID = 1;

    @Test
    public void testCancelBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.ACCEPTED);
        existingDriver.setLanguage(Language.ENGLISH);
        existingVehicle.setDriver(existingDriver);
        existingBooking.setVehicle(existingVehicle);

        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);  // Get the first arg of method call, in this case is existingBooking
            booking.setState(BookingState.CANCELED);
            return null;
        }).when(bookingDao).cancelBooking(existingBooking);

        Booking canceledBooking = clientService.cancelBooking(existingClient, BOOKING_ID);

        assertEquals(canceledBooking.getState(), BookingState.CANCELED);
    }

    @Test(expected = ForbiddenClientCancelBookingException.class)
    public void testCancelForbiddenBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setState(BookingState.FINISHED); // Since not ACCEPTED, it will throw an exception
        existingBooking.setVehicle(existingVehicle);

        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);

        clientService.cancelBooking(existingClient, BOOKING_ID);

        fail();
    }

    // There's not point in testing methods that are only wrappers to their daos because they were already tested in persistence

}
