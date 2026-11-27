package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenBookingStateOperationException;
import ar.edu.itba.paw.exceptions.ForbiddenClientCancelBookingException;
import ar.edu.itba.paw.exceptions.ReviewAlreadySentException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.BookingDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceImplTest {
    //This unused Service needs to exist for the test
    @Mock
    private MailService mailService;
    @Mock
    private BookingDao bookingDao;

    @InjectMocks
    private ClientServiceImpl clientService;

    private static final Integer BOOKING_ID = 1;
    private static final Integer RATING = 5;
    private static final String REVIEW = "Great service";

    @Test
    public void testCancelBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.ACCEPTED);
        existingDriver.setLanguage(Language.ENGLISH);

        when(existingVehicle.getDriver()).thenReturn(existingDriver);
        existingBooking.setVehicle(existingVehicle);

        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);  // Get the first arg of method call, in this case is existingBooking
            booking.setState(BookingState.CANCELED);
            return null;
        }).when(bookingDao).cancelBooking(existingBooking);

        Booking canceledBooking = clientService.cancelBooking(existingClient, BOOKING_ID);

        assertEquals(BookingState.CANCELED, canceledBooking.getState());
    }

    @Test(expected = ForbiddenClientCancelBookingException.class)
    public void testCancelForbiddenBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        existingBooking.setState(BookingState.FINISHED); // Since not ACCEPTED, it will throw an exception

        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);

        clientService.cancelBooking(existingClient, BOOKING_ID);

        fail();
    }

    @Test(expected = ReviewAlreadySentException.class)
    public void testSetBookingRatingAndReviewAlreadySent() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        existingBooking.setState(BookingState.FINISHED);
        existingBooking.setRating(4); // Already has a rating, so it will throw an exception

        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);
        clientService.setBookingRatingAndReview(existingClient, BOOKING_ID, RATING, REVIEW);

        fail();
    }

    @Test(expected = ForbiddenBookingStateOperationException.class)
    public void testSetBookingRatingAndReviewForbiddenState() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        existingBooking.setState(BookingState.ACCEPTED); // Since not FINISHED, it will throw an exception

        when(bookingDao.getClientBookingById(existingClient, BOOKING_ID)).thenReturn(existingBooking);
        clientService.setBookingRatingAndReview(existingClient, BOOKING_ID, RATING, REVIEW);

        fail();
    }

    // There's not point in testing methods that are only wrappers to their daos because they were already tested in persistence

}
