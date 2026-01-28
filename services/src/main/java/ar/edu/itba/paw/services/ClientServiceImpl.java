package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidVehicleException;
import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ClientServiceImpl extends UserServiceImpl<Client> implements ClientService {
    private final ClientDao clientDao;

    private final VehicleDao vehicleDao;

    private final ZoneDao zoneDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    public ClientServiceImpl(
            UserDao<Client> userDao,
            ClientDao clientDao,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            BookingDao bookingDao,
            VehicleDao vehicleDao,
            ZoneDao zoneDao
    ) {
        super(userDao, passwordEncoder, mailService, bookingDao);
        this.clientDao = clientDao;
        this.vehicleDao = vehicleDao;
        this.zoneDao = zoneDao;
    }

    @Transactional
    @Override
    public Client create(String username, String mail, String password, int zoneId, Locale locale) {
        checkRepeatedUser(username, mail);
        Zone zone = zoneDao.getZone(zoneId).orElseThrow(ZoneNotFoundException::new);
        Client client = clientDao.create(username, mail, passwordEncoder.encode(password), zone, Language.fromLocale(locale));
        LOGGER.info("Successfully created {} client", username);
        mailService.sendClientWelcomeMail(mail, username, locale);
        return client;
    }

    @Transactional
    @Override
    public Optional<Client> findByUsername(String username) {
        return clientDao.findByUsername(username);
    }

    @Override
    public Booking getBookingById(Client user, int id) {
        return bookingDao.getClientBookingById(user, id);
    }

    @Transactional
    @Override
    public Booking appointBooking(
            int vehicleId,
            Client client,
            int zoneId,
            int destinationId,
            LocalDate date,
            ShiftPeriod shiftPeriod,
            String jobDescription
    ) {
        Vehicle v = vehicleDao.findById(vehicleId).orElseThrow();
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        Zone destination = zoneDao.getZone(destinationId).orElseThrow();
        Booking booking = bookingDao.appointBooking(
                v, client, zone, destination, date, shiftPeriod, jobDescription
        );
        LOGGER.info("Successfully appointed booking for {} at {} on period {}", client.getUsername(), date.toString(), shiftPeriod.toString());
        mailService.sendRequestedDriverService(
                booking.getDriver().getUsername(), booking.getDriver().getMail(),
                booking.getClient().getUsername(), booking.getClient().getMail(),
                date, jobDescription, booking.getOriginZone().getNeighborhoodName(), booking.getDestinationZone().getNeighborhoodName(), booking.getShiftPeriod(),
                v.getDriver().getLanguage().getLocale(),
                client.getLanguage().getLocale());
        return booking;
    }

    @Transactional
    @Override
    public List<Booking> getBookings(Client client, BookingState state, int page) {
        if (state.equals(BookingState.PENDING) || state.equals(BookingState.REJECTED))
            bookingDao.checkPending();
        return bookingDao.getClientBookings(client, state, (page - 1) * Pagination.BOOKINGS_PAGE_SIZE);
    }

    @Transactional
    @Override
    public int getBookingCount(Client client, BookingState state) {
        return bookingDao.getClientBookingCount(client, state);
    }

    @Transactional
    @Override
    public void setBookingRatingAndReview(Client user, int bookingId, int rating, String review) {
        bookingDao.setRatingAndReview(getBookingById(user, bookingId), rating, review);
        LOGGER.info("Sent review for booking {}", bookingId);
    }

    @Transactional
    @Override
    public void editProfile(Client client, String username, String mail, Integer zoneId, String language) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow(ZoneNotFoundException::new);
        clientDao.editProfile(client, username, mail, zone, Language.valueOf(language));
        LOGGER.info("{} edited their profile", username);
    }

    @Transactional
    @Override
    public Booking cancelBooking(Client client, int bookingId) {
        Booking booking = super.cancelBooking(client, bookingId);
        mailService.sendClientCanceledBooking(
                booking.getDate(),
                booking.getDriver().getUsername(),
                booking.getDriver().getMail(),
                booking.getDriver().getLanguage().getLocale()
        );
        return booking;
    }

    @Override
    public List<ShiftPeriod> requestedShiftPeriodsForDate(Client client, LocalDate date, String plateNumber) {
        Vehicle v = vehicleDao.findByPlateNumber(plateNumber).orElseThrow(InvalidVehicleException::new);
        List<Booking> bookings = bookingDao.requestedBookingsForDate(client, date, v);
        return bookings.stream().map(Booking::getShiftPeriod).toList();
    }
}