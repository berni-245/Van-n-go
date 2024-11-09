package ar.edu.itba.paw.services;

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
    public Client create(String username, String mail, String password, Locale locale) {
        Client client = clientDao.create(username, username, passwordEncoder.encode(password), Language.fromLocale(locale));
        LOGGER.info("Successfully created {} client", username);
        mailService.sendClientWelcomeMail(mail, username, locale);
        return client;
    }

    @Transactional
    @Override
    public Optional<Client> findByUsername(String username) {
        return clientDao.findByUsername(username);
    }

    @Transactional
    @Override
    public Booking appointBooking(
            long vehicleId,
            Client client,
            long zoneId,
            long destinationId,
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
                Locale.of(v.getDriver().getLanguage().toLocale()),
                Locale.of(client.getLanguage().toLocale()));
        return booking;
    }

    @Transactional
    @Override
    public List<Booking> getBookings(Client client, BookingState state, int page) {
        return bookingDao.getClientBookings(client, state, (page - 1) * Pagination.BOOKINGS_PAGE_SIZE);
    }

    @Transactional
    @Override
    public long getBookingCount(Client client, BookingState state) {
        return bookingDao.getClientBookingCount(client, state);
    }

    @Transactional
    @Override
    public void setBookingRatingAndReview(long bookingId, int rating, String review) {
        //send mail!
        bookingDao.setRatingAndReview(bookingDao.getBookingById(bookingId).orElseThrow(), rating, review);
        LOGGER.info("Sent review for booking {}", bookingId);
    }

    @Transactional
    @Override
    public void editProfile(Client client, String username, String mail, Long zoneId, String language) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        Language lang = Language.valueOf(language);
        clientDao.editProfile(client, username, mail, zone, lang);
        LOGGER.info("{} edited their profile", username);
    }

    @Transactional
    @Override
    public Booking cancelBooking(long bookingId, Client client) {
        Booking booking = super.cancelBooking(bookingId, client);
        mailService.sendClientCanceledBooking(
                booking.getDate(),
                booking.getDriver().getUsername(),
                booking.getDriver().getMail(),
                Locale.of(booking.getDriver().getLanguage().toLocale())
        );
        return booking;
    }
}