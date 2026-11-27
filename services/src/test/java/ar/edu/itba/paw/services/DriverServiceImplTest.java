package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    private VehicleService vehicleService;
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

    private Driver existingDriver;
    private Vehicle expectedVehicle;

    @Before
    public void setUp() {
        existingDriver = new Driver("driver", "mail@mail.com", "pass", "desc", null, "123", Language.SPANISH);
        expectedVehicle = new Vehicle(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, null, RATE);
    }



    @Test
    public void testAddVehicleSuccessful() {
        when(vehicleDao.getVehicleCount(existingDriver)).thenReturn(LOW_VEHICLE_COUNT);
        when(vehicleService.plateNumberExists(PLATE_NUMBER)).thenReturn(false);

        when(vehicleDao.create(
                eq(existingDriver), eq(PLATE_NUMBER), eq(VOLUME), eq(DESCRIPTION), eq(RATE)
        )).thenReturn(expectedVehicle);


        Vehicle result = driverService.addVehicle(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, RATE);

        assertNotNull(result);
        assertEquals(expectedVehicle.getPlateNumber(), result.getPlateNumber());

        // Verificamos que se haya llamado al DAO exactamente una vez
        verify(vehicleDao).create(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, RATE);
    }

    @Test(expected = DriverVehicleLimitReachedException.class)
    public void testAddVehicleExceedsLimit() {
        when(vehicleDao.getVehicleCount(existingDriver)).thenReturn(EXCEEDED_VEHICLE_COUNT);

        driverService.addVehicle(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, RATE);

        fail();
    }

    @Test(expected = PlateNumberAlreadyExistsException.class)
    public void testAddVehiclePlateAlreadyExists() {
        when(vehicleDao.getVehicleCount(existingDriver)).thenReturn(LOW_VEHICLE_COUNT);
        when(vehicleService.plateNumberExists(PLATE_NUMBER)).thenReturn(true);

        driverService.addVehicle(existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, RATE);

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

        assertEquals(BookingState.ACCEPTED, existingBooking.getState());
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

        assertEquals(BookingState.REJECTED, existingBooking.getState());
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
        existingBooking.setDate(LocalDate.now().minusDays(1));
        when(existingBooking.getDriver()).thenReturn(existingDriver);

        when(bookingDao.getDriverBookingById(existingDriver, BOOKING_ID)).thenReturn(existingBooking);

        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setState(BookingState.FINISHED);
            return null;
        }).when(bookingDao).finishBooking(existingBooking);

        driverService.finishBooking(existingDriver, BOOKING_ID);

        assertEquals(BookingState.FINISHED, existingBooking.getState());
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

        assertEquals(BookingState.CANCELED, canceledBooking.getState());
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

    @Test(expected = VehicleNotFoundException.class)
    public void testUpdateVehicleNotFound() {
        when(vehicleDao.findById(10)).thenReturn(Optional.empty());
        driverService.updateVehicle(existingDriver, 10, "NEW999", VOLUME, DESCRIPTION, RATE);
    }

    @Test(expected = PlateNumberAlreadyExistsException.class)
    public void testUpdateVehiclePlateAlreadyExists() {
        Vehicle vehicleWithId = new Vehicle(10, existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, null, RATE);
        when(vehicleDao.findById(10)).thenReturn(Optional.of(vehicleWithId));
        when(vehicleService.plateNumberExists("NEW999")).thenReturn(true);
        driverService.updateVehicle(existingDriver, 10, "NEW999", VOLUME, DESCRIPTION, RATE);
    }


    @Test
    public void testUpdatePictureSuccessful() {
        Vehicle vehicleWithId = new Vehicle(10, existingDriver, PLATE_NUMBER, VOLUME, DESCRIPTION, null, RATE);
        when(vehicleDao.findById(10)).thenReturn(Optional.of(vehicleWithId));

        byte[] bytes = new byte[]{1, 2, 3};
        driverService.updatePicture(vehicleWithId, "foto.png", bytes, "image/png");

        verify(imageDao).uploadVehicleImage(bytes, "foto.png", vehicleWithId, "image/png");
    }

    @Test(expected = VehicleHasAcceptedJobsException.class)
    public void testDeleteVehicleWithAcceptedJobs() {
        Vehicle vehicle = spy(expectedVehicle);
        when(bookingDao.hasAnAcceptedBooking(vehicle)).thenReturn(true);
        driverService.deleteVehicle(vehicle);
    }

    // There's not point in testing methods that are only wrappers to their daos because they were already tested in persistence
}
