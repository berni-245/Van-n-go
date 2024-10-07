package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.HourInterval;
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

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = {
        "classpath:db_init.sql", // Adds constant data to tables
        "classpath:add_availability_data.sql" // There will be a vehicle with id 1, who works from 0-4am, on wednesday and thursday in the zone with id 1
})
public class BookingJdbcDaoTest {
    // This booking will accepted and for ve.id = 1, zone.id = 1 on thursday, between 1-3am
    private static final long PREEXISTING_ACCEPTED_BOOK = 500;
    // This booking will pending and for ve.id = 1, zone.id = 1, on friday, between 1-3am
    private static final long PREEXISTING_PENDING_BOOK = 501;
    private static final long ANOTHER_PREEXISTING_PENDING_BOOK = 502;
    private static final long BOOKING_COUNT = 3;


    private static final long VEHICLE_ID = 1;
    private static final long CLIENT_ID = 1;
    private static final long CLIENT_ID_TWO = 2;
    private static final long DRIVER_ID = 3;
    private static final long ZONE_ID = 1;
    private static final long ZONE_THAT_DRIVER_DOES_NOT_WORK = 2;
    private static final LocalDate DATE_FREE_TO_APPOINT = LocalDate.of(2030, 5, 1);
    private static final LocalDate ALREADY_ACCEPTED_BOOKED_DATE = LocalDate.of(2030, 5, 2);
    private static final LocalDate ALREADY_PENDING_BOOKED_DATE = LocalDate.of(2030, 5, 3);
    private static final int RATING = 5;

    @Autowired
    private BookingJdbcDao bookingDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testSuccessfulAppointBooking() {
        HourInterval hourInterval = new HourInterval(0, 4);
        Optional<Booking> optionalBooking = bookingDao.appointBooking(VEHICLE_ID, CLIENT_ID, ZONE_ID, DATE_FREE_TO_APPOINT, hourInterval);

        assertTrue(optionalBooking.isPresent());
        Booking booking = optionalBooking.get();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("""
                             id = '%d' and date = '%s' and hour_start_id = '%d' and
                             hour_end_id = '%d' and client_id = '%d' and vehicle_id = '%d' and
                              zone_id = '%d' and state = '%s'""",
                        booking.getBookingId(), DATE_FREE_TO_APPOINT, hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        CLIENT_ID, VEHICLE_ID, ZONE_ID, BookingState.PENDING)));
    }

    // Remember that there's a accepted booking for the same client in ALREADY_ACCEPTED_BOOKED_DATE for 1-3 am

    @Test
    public void testAppointBookingOnTopOfAnotherAcceptedBooking() {
        Optional<Booking> onTopOfAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_ACCEPTED_BOOKED_DATE, new HourInterval(0, 4)
        );
        assertTrue(onTopOfAnotherBooking.isEmpty());
    }

    @Test
    public void testAppointBookingEqualsToAnotherAcceptedBooking() {
        Optional<Booking> equalsToAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_ACCEPTED_BOOKED_DATE, new HourInterval(1, 3)
        );
        assertTrue(equalsToAnotherBooking.isEmpty());
    }

    @Test
    public void testAppointBookingInsideOfAnotherAcceptedBooking() {
        Optional<Booking> insideOfAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_ACCEPTED_BOOKED_DATE, new HourInterval(1, 2)
        );
        assertTrue(insideOfAnotherBooking.isEmpty());
    }

    @Test
    public void testAppointBookingExactlyBeforeAnotherAcceptedBooking() {
        Optional<Booking> exactlyBeforeAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_ACCEPTED_BOOKED_DATE, new HourInterval(0, 1)
        );
        assertTrue(exactlyBeforeAnotherBooking.isPresent());
    }

    @Test
    public void testAppointBookingExactlyAfterAnotherAcceptedBooking() {
        Optional<Booking> exactlyAfterAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_ACCEPTED_BOOKED_DATE, new HourInterval(3, 4)
        );
        assertTrue(exactlyAfterAnotherBooking.isPresent());
    }

    // Remember that there's a pending booking for the same client (ID = 1) in ALREADY_PENDING_BOOKED_DATE for 1-3 am

    @Test
    public void testAppointBookingOnTopOfAnotherBookingByTheSameClient() {
        Optional<Booking> onTopOfAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_PENDING_BOOKED_DATE, new HourInterval(0, 4)
        );
        assertTrue(onTopOfAnotherBooking.isEmpty());
    }

    @Test
    public void testAppointBookingEqualsToAnotherBookingByTheSameClient() {
        Optional<Booking> equalsToAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_PENDING_BOOKED_DATE, new HourInterval(1, 3)
        );
        assertTrue(equalsToAnotherBooking.isEmpty());
    }

    @Test
    public void testAppointBookingInsideOfAnotherBookingByTheSameClient() {
        Optional<Booking> insideOfAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_PENDING_BOOKED_DATE, new HourInterval(1, 2)
        );
        assertTrue(insideOfAnotherBooking.isEmpty());
    }

    @Test
    public void testAppointBookingExactlyBeforeAnotherBookingByTheSameClient() {
        Optional<Booking> exactlyBeforeAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_PENDING_BOOKED_DATE, new HourInterval(0, 1)
        );
        assertTrue(exactlyBeforeAnotherBooking.isPresent());
    }

    @Test
    public void testAppointBookingExactlyAfterAnotherBookingByTheSameClient() {
        Optional<Booking> exactlyAfterAnotherBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, ALREADY_PENDING_BOOKED_DATE, new HourInterval(3, 4)
        );
        assertTrue(exactlyAfterAnotherBooking.isPresent());
    }

    // Remember that there's a pending booking for the client ID = 1 in ALREADY_PENDING_BOOKED_DATE for 1-3 am

    @Test
    public void testAppointBookingOnAlreadyAppointedTimeForDifferentClients() {
        //Two different clients can appoint for the same time, as long as there's no accepted booking for those intervals

        Booking secondBooking = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID_TWO, ZONE_ID, ALREADY_PENDING_BOOKED_DATE, new HourInterval(0, 4)
        ).orElseThrow();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        secondBooking.getBookingId(), BookingState.PENDING.name())));
    }

    @Test
    public void testAppointBookingWrongTimeForVehicle() {
        Optional<Booking> wrongTime = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_ID, DATE_FREE_TO_APPOINT, new HourInterval(0, 24)
        );
        assertTrue(wrongTime.isEmpty());
    }

    @Test
    public void testAppointBookingWrongZoneForVehicle() {
        Optional<Booking> wrongZone = bookingDao.appointBooking(
                VEHICLE_ID, CLIENT_ID, ZONE_THAT_DRIVER_DOES_NOT_WORK, DATE_FREE_TO_APPOINT, new HourInterval(0, 1)
        );
        assertTrue(wrongZone.isEmpty());
    }


    @Test
    public void testGetBookingById() {
        Optional<Booking> optionalBooking = bookingDao.getBookingById(PREEXISTING_ACCEPTED_BOOK);

        assertTrue(optionalBooking.isPresent());
        Booking booking = optionalBooking.get();
        assertEquals(PREEXISTING_ACCEPTED_BOOK, booking.getBookingId());
    }

    @Test
    public void testGetBookingsByDriver() {
        List<Booking> bookings = bookingDao.getBookings(DRIVER_ID);

        assertEquals(BOOKING_COUNT, bookings.size());
        Iterator<Booking> iterator = bookings.iterator();
        assertEquals(PREEXISTING_ACCEPTED_BOOK, iterator.next().getBookingId());
        assertEquals(PREEXISTING_PENDING_BOOK, iterator.next().getBookingId());
        assertEquals(ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getBookingId());
    }

    @Test
    public void testGetBookingsByClient() {
        List<Booking> bookings = bookingDao.getClientBookings(CLIENT_ID);

        assertEquals(BOOKING_COUNT, bookings.size());
        Iterator<Booking> iterator = bookings.iterator();
        assertEquals(PREEXISTING_ACCEPTED_BOOK, iterator.next().getBookingId());
        assertEquals(PREEXISTING_PENDING_BOOK, iterator.next().getBookingId());
        assertEquals(ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getBookingId());
    }

    @Test
    public void testGetBookingsByVehicle() {
        List<Booking> bookings = bookingDao.getBookingsByVehicle(VEHICLE_ID);

        assertEquals(BOOKING_COUNT, bookings.size());
        Iterator<Booking> iterator = bookings.iterator();
        assertEquals(PREEXISTING_ACCEPTED_BOOK, iterator.next().getBookingId());
        assertEquals(PREEXISTING_PENDING_BOOK, iterator.next().getBookingId());
        assertEquals(ANOTHER_PREEXISTING_PENDING_BOOK, iterator.next().getBookingId());
    }

    @Test
    public void testAcceptBooking() {
        bookingDao.acceptBooking(PREEXISTING_PENDING_BOOK);

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
        bookingDao.rejectBooking(PREEXISTING_PENDING_BOOK);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and state = '%s'",
                        PREEXISTING_PENDING_BOOK, BookingState.REJECTED.name())));
    }

    @Test
    public void testSetRatingAndReview() {
        bookingDao.setRatingAndReview(PREEXISTING_ACCEPTED_BOOK, RATING, "Amazing");

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, "booking",
                String.format("id = '%d' and rating = '%d'",
                        PREEXISTING_ACCEPTED_BOOK, RATING)));
    }


}