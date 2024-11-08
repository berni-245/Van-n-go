package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidUserOnBookingCancelException;
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
public class ClientServiceImpl extends UserServiceImpl implements ClientService {
    @Autowired
    private ClientDao clientDao;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private ZoneDao zoneDao;

    private final PasswordEncoder passwordEncoder;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    public ClientServiceImpl(
            UserDao userDao,
            ClientDao clientDao,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            BookingDao bookingDao,
            VehicleDao vehicleDao,
            ZoneDao zoneDao
    ) {
        super(userDao, passwordEncoder, mailService);
        this.clientDao = clientDao;
        this.bookingDao = bookingDao;
        this.vehicleDao = vehicleDao;
        this.zoneDao = zoneDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public Client create(String username, String mail, String password, Locale locale) {
        Client client = clientDao.create(username, username, passwordEncoder.encode(password), getLanguageFromLocale(locale));
        LOGGER.info("Successfully created {} client", username);
        mailService.sendClientWelcomeMail(mail, username, locale);
        return client;
    }

    @Override
    public Optional<Client> findById(long id) {
        return clientDao.findById(id);
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
            String jobDescription,
            Locale locale
    ) {
        Vehicle v = vehicleDao.findById(vehicleId).orElseThrow();
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        Zone destination = zoneDao.getZone(destinationId).orElseThrow();
        Booking booking = bookingDao.appointBooking(
                    v, client, zone, destination, date, shiftPeriod, jobDescription
        );
        LOGGER.info("Successfully appointed booking for {} at {} on period {}", client.getUsername(), date.toString(), shiftPeriod.toString());
        mailService.sendRequestedDriverService(
                booking.getDriver().getUsername(),booking.getDriver().getMail(),
                booking.getClient().getUsername(), booking.getClient().getMail(),
                date, jobDescription, booking.getOriginZone().getNeighborhoodName(),booking.getDestinationZone().getNeighborhoodName(),booking.getShiftPeriod() ,locale );
        return booking;
    }

    @Override
    public List<Booking> getBookings(Client client, BookingState state, int page) {
        return bookingDao.getClientBookings(client, state, (page - 1) * Pagination.BOOKINGS_PAGE_SIZE);
    }

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
    public void editProfile(Client client, String username, String mail, Long zoneId) {
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        clientDao.editProfile(client, username, mail, zone);
        LOGGER.info("{} edited it's profile", username);
    }

    @Transactional
    @Override
    public void cancelBooking(long bookingId, Client client, Locale locale) {
        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        if(! booking.getClient().equals(client))
            throw new InvalidUserOnBookingCancelException();

        bookingDao.cancelBooking(booking);
        mailService.sendClientCanceledBooking(booking.getDate(),booking.getDriver().getUsername(),booking.getDriver().getMail(),locale);
        LOGGER.info("{} canceled booking {}", client.getUsername(), bookingId);
    }
}
