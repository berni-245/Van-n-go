package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.ClientAlreadyAppointedException;
import ar.edu.itba.paw.exceptions.TimeAlreadyPassedException;
import ar.edu.itba.paw.exceptions.VehicleIsAlreadyAcceptedException;
import ar.edu.itba.paw.exceptions.VehicleNotAvailableException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:add_booking_data.sql")
// There will be a vehicle with id 1, who works in the zone with id 1
// This driver works:
//  evening on wednesday
//  afternoon and evening on thursday
//  morning, afternoon and evening on friday

public class BookingJpaDaoTest {
    // This booking will accepted and for ve.id = 1, zone.id = 1 on thursday, EVENING
    private static final int PREEXISTING_ACCEPTED_BOOK = 500;
    private static final LocalDate ALREADY_ACCEPTED_BOOKED_DATE = LocalDate.of(2030, 5, 2); // It's thursday

    // This booking will pending and for ve.id = 1, zone.id = 1, on friday, EVENING
    private static final int PREEXISTING_PENDING_BOOK = 501;
    private static final int ANOTHER_PREEXISTING_PENDING_BOOK = 502;
    private static final int YET_ANOTHER_PREEXISTING_PENDING_BOOK = 503;
    private static final int BOOKING_COUNT = 4;
    private static final int DRIVER_BOOKING_PENDING_COUNT = 3;
    private static final int CLIENT_BOOKING_PENDING_COUNT = 2;

    private static final int VEHICLE_ID = 1;
    private static final int CLIENT_ID = 1;
    private static final int CLIENT_ID_TWO = 2;
    private static final int DRIVER_ID = 3;
    private static final int ORIGIN_ZONE_ID = 1;
    private static final int DESTINATION_ZONE_ID = 23;
    private static final int ZONE_THAT_DRIVER_DOES_NOT_WORK = 2;

    private static final LocalDate DATE_FREE_TO_APPOINT = LocalDate.of(2030, 5, 1); // It's a wednesday
    private static final LocalDate ALREADY_PENDING_BOOKED_DATE = LocalDate.of(2030, 5, 3); // It's a friday
    private static final LocalDate DATE_OLDER_THAN_NOW = LocalDate.of(2024, 11, 6); // It's wednesday

    private static final int RATING = 5;
    private static final String JOB_DESCRIPTION = "This is a job description";

    private static Vehicle vehicle;
    private static Client client;
    private static Client clientTwo;
    private static Zone originZone;
    private static Zone destinationZone;
    private static Zone zoneThatDriverDoesNotWork;
    private static Driver driver;
    private static Booking preexistingPendingBooking;
    private static Booking preexistingAcceptedBooking;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private BookingJpaDao bookingDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        vehicle = em.find(Vehicle.class, VEHICLE_ID);
        client = em.find(Client.class, CLIENT_ID);
        originZone = em.find(Zone.class, ORIGIN_ZONE_ID);
        destinationZone = em.find(Zone.class, DESTINATION_ZONE_ID);
        zoneThatDriverDoesNotWork = em.find(Zone.class, ZONE_THAT_DRIVER_DOES_NOT_WORK);
        clientTwo = em.find(Client.class, CLIENT_ID_TWO);
        driver = em.find(Driver.class, DRIVER_ID);
        preexistingPendingBooking = em.find(Booking.class, PREEXISTING_PENDING_BOOK);
        preexistingAcceptedBooking = em.find(Booking.class, PREEXISTING_ACCEPTED_BOOK);
    }

    @Test
    public void testSuccessfulAppointBooking() {
        Booking booking = bookingDao.appointBooking(vehicle, client, originZone, destinationZone, DATE_FREE_TO_APPOINT, ShiftPeriod.EVENING, JOB_DESCRIPTION);

        assertNotNull(booking);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("""
                                id = '%d' and date = '%s' and shift_period = '%s' and
                                client_id = '%d' and vehicle_id = '%d' and
                                origin_zone_id = '%d' and destination_zone_id = '%d' and state = '%s'""",
                        booking.getId(), DATE_FREE_TO_APPOINT, ShiftPeriod.EVENING.name(),
                        CLIENT_ID, VEHICLE_ID,
                        ORIGIN_ZONE_ID, DESTINATION_ZONE_ID, BookingState.PENDING.name())));
    }

    // Remember that there's an accepted booking for another client in ALREADY_ACCEPTED_BOOKED_DATE for EVENING

    @Test
    public void testAppointBookingOnTopOfAnotherAcceptedBooking() {
        assertThrows(
                VehicleIsAlreadyAcceptedException.class,
                () -> bookingDao.appointBooking(
                    vehicle, client, originZone, destinationZone, ALREADY_ACCEPTED_BOOKED_DATE, ShiftPeriod.EVENING, JOB_DESCRIPTION
                )
        );
    }

    @Test
    public void testAppointBookingOnSameDayAsAnotherAcceptedBookingButDifferentShiftPeriod() {
        Booking booking = bookingDao.appointBooking(
                vehicle, client, originZone, destinationZone, ALREADY_ACCEPTED_BOOKED_DATE, ShiftPeriod.AFTERNOON, JOB_DESCRIPTION
        );

        assertNotNull(booking);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("""
                                id = '%d' and date = '%s' and shift_period = '%s' and
                                client_id = '%d' and vehicle_id = '%d' and
                                origin_zone_id = '%d' and destination_zone_id = '%d' and state = '%s'""",
                        booking.getId(), ALREADY_ACCEPTED_BOOKED_DATE, ShiftPeriod.AFTERNOON.name(),
                        CLIENT_ID, VEHICLE_ID,
                        ORIGIN_ZONE_ID, DESTINATION_ZONE_ID, BookingState.PENDING.name())));
    }

    // Remember that there's a pending booking for the same client (ID = 1) in ALREADY_PENDING_BOOKED_DATE for EVENING

    @Test
    public void testAppointBookingOnTopOfAnotherBookingByTheSameClient() {
        assertThrows(
                ClientAlreadyAppointedException.class,
                () -> bookingDao.appointBooking(
                        vehicle, client, originZone, destinationZone, ALREADY_PENDING_BOOKED_DATE, ShiftPeriod.EVENING, JOB_DESCRIPTION
                )
        );
    }


    @Test
    public void testAppointBookingOnSameDayAsAnotherPendingBookingByTheSameClientButDifferentShiftPeriod() {
        Booking booking = bookingDao.appointBooking(
                vehicle, client, originZone, destinationZone, ALREADY_PENDING_BOOKED_DATE, ShiftPeriod.MORNING, JOB_DESCRIPTION
        );
        assertNotNull(booking);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("""
                                id = '%d' and date = '%s' and shift_period = '%s' and
                                client_id = '%d' and vehicle_id = '%d' and
                                origin_zone_id = '%d' and destination_zone_id = '%d' and state = '%s'""",
                        booking.getId(), ALREADY_PENDING_BOOKED_DATE, ShiftPeriod.MORNING.name(),
                        CLIENT_ID, VEHICLE_ID,
                        ORIGIN_ZONE_ID, DESTINATION_ZONE_ID, BookingState.PENDING.name())
                )
        );
    }

    // Remember that there's a pending booking for the client ID = 1 in ALREADY_PENDING_BOOKED_DATE for EVENING

    @Test
    public void testAppointBookingOnAlreadyAppointedShiftPeriodForDifferentClients() {
        //Two different clients can appoint for the same time, as int as there's no accepted booking for that shift period

        Booking secondBooking = bookingDao.appointBooking(
                vehicle, clientTwo, originZone, destinationZone, ALREADY_PENDING_BOOKED_DATE, ShiftPeriod.EVENING, JOB_DESCRIPTION
        );

        assertNotNull(secondBooking);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        secondBooking.getId(), BookingState.PENDING.name())));
    }

    // Remember that vehicle id 1 only works on EVENING in DATE_FREE_TO_APPOINT in zone id 1

    @Test
    public void testAppointBookingWrongTimeForVehicle() {
        assertThrows(
                VehicleNotAvailableException.class,
                () -> bookingDao.appointBooking(
                        vehicle, client, originZone, destinationZone, DATE_FREE_TO_APPOINT, ShiftPeriod.MORNING, JOB_DESCRIPTION
                )
        );
    }

    @Test
    public void testAppointBookingWrongZoneForVehicle() {
        assertThrows(
                VehicleNotAvailableException.class,
                () -> bookingDao.appointBooking(
                        vehicle, client, zoneThatDriverDoesNotWork, destinationZone, DATE_FREE_TO_APPOINT, ShiftPeriod.EVENING, JOB_DESCRIPTION
                )
        );
    }

    @Test
    public void testAppointBookingDayPreviousToNow() {
        assertThrows(
                TimeAlreadyPassedException.class,
                () -> bookingDao.appointBooking(
                        vehicle, client, originZone, destinationZone, DATE_OLDER_THAN_NOW, ShiftPeriod.EVENING, JOB_DESCRIPTION
                )

        );
    }

    @Test
    public void testGetBookingById() {
        Optional<Booking> optionalBooking = bookingDao.getBookingById(PREEXISTING_ACCEPTED_BOOK);
        assertTrue(optionalBooking.isPresent());
        Booking booking = optionalBooking.get();
        assertEquals(PREEXISTING_ACCEPTED_BOOK, booking.getId());
    }
/*
    @Test
    public void testGetBookingsByDriver() {
        // TODO this fails because in that method we assume we have int in the database and
        // in the db generated by hibernated used to test we have bigint
        List<Booking> bookings = bookingDao.getDriverBookings(driver, BookingState.PENDING, 0);

        assertEquals(DRIVER_BOOKING_PENDING_COUNT, bookings.size());

        Iterator<Booking> iterator = bookings.iterator();
        assertEquals(PREEXISTING_PENDING_BOOK, iterator.next().getId());
        assertEquals(ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getId());
        assertEquals(YET_ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getId());
    }

        // TODO this fails because in that method we assume we have int in the database and
        // in the db generated by hibernated used to test we have bigint
    @Test
    public void testGetBookingsByClient() {
        List<Booking> bookings = bookingDao.getClientBookings(client, BookingState.PENDING, 0);
        assertEquals(CLIENT_BOOKING_PENDING_COUNT, bookings.size());
        assertEquals(PREEXISTING_PENDING_BOOK, bookings.getFirst().getId());
        assertEquals(YET_ANOTHER_PREEXISTING_PENDING_BOOK, bookings.getFirst().getId());
    }
*/
    @Test
    public void testGetBookingsByVehicle() {
        List<Booking> bookings = bookingDao.getBookingsByVehicle(vehicle);

        assertEquals(BOOKING_COUNT, bookings.size());
        Iterator<Booking> iterator = bookings.iterator();
        assertEquals(PREEXISTING_ACCEPTED_BOOK, iterator.next().getId());
        assertEquals(PREEXISTING_PENDING_BOOK, iterator.next().getId());
        assertEquals(ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getId());
        assertEquals(YET_ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getId());

    }

    @Test
    public void testAcceptBooking() {
        bookingDao.acceptBooking(preexistingPendingBooking);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        PREEXISTING_PENDING_BOOK, BookingState.ACCEPTED.name())));

        // Since this booking's interval collides with the first pending booking that was accepted, it will become rejected
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        ANOTHER_PREEXISTING_PENDING_BOOK, BookingState.REJECTED.name())));
    }

    @Test
    public void testRejectBooking() {
        bookingDao.rejectBooking(preexistingPendingBooking);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        PREEXISTING_PENDING_BOOK, BookingState.REJECTED.name())));
    }

    @Test
    public void testFinishBooking() {
        bookingDao.finishBooking(preexistingAcceptedBooking);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        PREEXISTING_ACCEPTED_BOOK, BookingState.FINISHED.name())));
    }

    @Test
    public void testCancelBooking() {
        bookingDao.cancelBooking(preexistingAcceptedBooking);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        PREEXISTING_ACCEPTED_BOOK, BookingState.CANCELED.name())));
    }

    @Test
    public void testSetRatingAndReview() {
        bookingDao.setRatingAndReview(preexistingAcceptedBooking, RATING, "Amazing");

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and rating = '%d'",
                        PREEXISTING_ACCEPTED_BOOK, RATING)));
    }

}