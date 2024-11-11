package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.DriverVehicleLimitReachedException;
import ar.edu.itba.paw.exceptions.ForbiddenBookingStateOperationException;
import ar.edu.itba.paw.exceptions.ForbiddenClientCancelBookingException;
import ar.edu.itba.paw.exceptions.ForbiddenDriverCancelBookingException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DriverServiceImplTest {
    @Mock
    private DriverDao driverDao;
    @Mock
    private VehicleDao vehicleDao;
    @Mock
    private MailService mailService;
    @Mock
    private ImageDao imageDao;
    @Mock
    private ZoneDao zoneDao;
    @Mock
    private BookingDao bookingDao;
    @Mock
    private AvailabilityDao availabilityDao;
    @InjectMocks
    private DriverServiceImpl driverService;

    private static final String PLATE_NUMBER = "AAA333";
    private static final double VOLUME = 100;
    private static final String DESCRIPTION = "lalala";
    private static final List<Integer> ZONE_IDS = List.of(1,2);
    private static final double RATE = 100;

    private static final Integer LOW_VEHICLE_COUNT = 1;
    private static final Integer EXCEEDED_VEHICLE_COUNT = 100;

    private static final Integer BOOKING_ID = 1;

    @Test
    public void testAddVehicle() {
        final Driver existingDriver = spy(Driver.class);
        final Zone zone1 = spy(Zone.class);
        final Zone zone2 = spy(Zone.class);

        final Vehicle resultVehicle = spy(Vehicle.class);

        when(vehicleDao.getVehicleCount(existingDriver)).thenReturn(LOW_VEHICLE_COUNT);
        when(zoneDao.getZonesById(ZONE_IDS)).thenReturn(List.of(zone1, zone2));

        when(vehicleDao.create(
                existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, List.of(zone1, zone2), RATE
            )
        ).thenReturn(resultVehicle);

        Vehicle result = driverService.addVehicle(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, ZONE_IDS, RATE, null, null);

        assertSame(resultVehicle, result);
        // No exception was thrown!
    }

    @Test(expected = DriverVehicleLimitReachedException.class)
    public void testAddVehicleLimitReached() {
        final Driver existingDriver = spy(Driver.class);

        when(vehicleDao.getVehicleCount(existingDriver)).thenReturn(EXCEEDED_VEHICLE_COUNT);

        driverService.addVehicle(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, ZONE_IDS, RATE, null, null);

        fail();

    }

    @Test
    public void getWorkingDays() {
        final Vehicle existingVehicle = spy(Vehicle.class);
        final Vehicle existingVehicle2 = spy(Vehicle.class);
        final Availability existingAvailability1 = spy(Availability.class);
        final Availability existingAvailability2 = spy(Availability.class);
        final Availability existingAvailability3 = spy(Availability.class);

        when(existingVehicle.getAvailability()).thenReturn(List.of(existingAvailability1));
        when(existingVehicle2.getAvailability()).thenReturn(List.of(existingAvailability2, existingAvailability3));
        when(existingAvailability1.getWeekDay()).thenReturn(DayOfWeek.WEDNESDAY);
        when(existingAvailability2.getWeekDay()).thenReturn(DayOfWeek.THURSDAY);
        when(existingAvailability3.getWeekDay()).thenReturn(DayOfWeek.WEDNESDAY);

        Set<DayOfWeek> workingDays = driverService.getWorkingDays(List.of(existingVehicle, existingVehicle2));
        assertTrue(workingDays.stream().anyMatch(day -> day.equals(DayOfWeek.WEDNESDAY)));
        assertTrue(workingDays.stream().anyMatch(day -> day.equals(DayOfWeek.THURSDAY)));
        assertEquals(2, workingDays.size());
    }

    @Test
    public void testAcceptBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setState(BookingState.PENDING);
        when(existingBooking.getClient()).thenReturn(existingClient);
        existingBooking.setVehicle(existingVehicle);
        when(existingBooking.getDriver()).thenReturn(existingDriver);
        existingClient.setLanguage(Language.ENGLISH);

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);  // Get the first arg of method call, in this case is existingBooking
            booking.setState(BookingState.ACCEPTED);
            return null;
        }).when(bookingDao).acceptBooking(existingBooking);

        driverService.acceptBooking(existingDriver, BOOKING_ID);

        assertEquals(existingBooking.getState(), BookingState.ACCEPTED);
    }

    @Test(expected = ForbiddenBookingStateOperationException.class)
    public void testAcceptBookingForbidden() {
        final Booking existingBooking = spy(Booking.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.CANCELED); // Since not PENDING, it will throw an exception

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        driverService.acceptBooking(existingDriver, BOOKING_ID);

        fail();
    }

    @Test
    public void testRejectBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setState(BookingState.PENDING);
        when(existingBooking.getClient()).thenReturn(existingClient);
        existingBooking.setVehicle(existingVehicle);
        when(existingBooking.getDriver()).thenReturn(existingDriver);
        existingClient.setLanguage(Language.ENGLISH);

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setState(BookingState.REJECTED);
            return null;
        }).when(bookingDao).rejectBooking(existingBooking);

        driverService.rejectBooking(existingDriver, BOOKING_ID);

        assertEquals(existingBooking.getState(), BookingState.REJECTED);
    }

    @Test(expected = ForbiddenBookingStateOperationException.class)
    public void testRejectBookingForbidden() {
        final Booking existingBooking = spy(Booking.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.CANCELED); // Since not PENDING, it will throw an exception

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        driverService.rejectBooking(existingDriver, BOOKING_ID);

        fail();
    }

    @Test
    public void testFinishBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Driver existingDriver = spy(Driver.class);
        final Vehicle existingVehicle = spy(Vehicle.class);
        existingBooking.setState(BookingState.ACCEPTED);
        existingBooking.setVehicle(existingVehicle);
        when(existingBooking.getDriver()).thenReturn(existingDriver);

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setState(BookingState.FINISHED);
            return null;
        }).when(bookingDao).finishBooking(existingBooking);

        driverService.finishBooking(existingDriver, BOOKING_ID);

        assertEquals(existingBooking.getState(), BookingState.FINISHED);
    }

    @Test(expected = ForbiddenBookingStateOperationException.class)
    public void testFinishBookingForbidden() {
        final Booking existingBooking = spy(Booking.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.CANCELED); // Since not ACCEPTED, it will throw an exception

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        driverService.finishBooking(existingDriver, BOOKING_ID);

        fail();
    }

    @Test
    public void testCancelBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Client existingClient = spy(Client.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.ACCEPTED);
        when(existingBooking.getClient()).thenReturn(existingClient);
        existingClient.setLanguage(Language.ENGLISH);

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setState(BookingState.CANCELED);
            return null;
        }).when(bookingDao).cancelBooking(existingBooking);

        Booking canceledBooking = driverService.cancelBooking(existingDriver, BOOKING_ID);

        assertEquals(canceledBooking.getState(), BookingState.CANCELED);
    }

    @Test(expected = ForbiddenDriverCancelBookingException.class)
    public void testCancelForbiddenBooking() {
        final Booking existingBooking = spy(Booking.class);
        final Driver existingDriver = spy(Driver.class);
        existingBooking.setState(BookingState.FINISHED); // Since not ACCEPTED, it will throw an exception

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        driverService.cancelBooking(existingDriver, BOOKING_ID);

        fail();
    }
}
