package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.*;
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
    }

    @Transactional
    @Override
    public Client create(String username, String mail, String password, Locale locale) {
               // Client instance will be created with unencrypted password.
        // Is that a problem tho?
        Client client = clientDao.create(username,username,password);
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
            LocalDate date,
            ShiftPeriod shiftPeriod,
            String jobDescription,
            Locale locale
    ) {
        Vehicle v = vehicleDao.findById(vehicleId).orElseThrow();
        Zone zone = zoneDao.getZone(zoneId).orElseThrow();
        Booking booking = bookingDao.appointBooking(
                v, client, zone, date, shiftPeriod, jobDescription
        );
        mailService.sendRequestedDriverService(
                booking.getDriver().getId(), client, date, jobDescription, locale
        );
        return booking;
    }

    @Override
    public List<Booking> getBookings(long id, int page) {
        return bookingDao.getClientBookings(id, Pagination.BOOKINGS_PAGE_SIZE * page);
    }

    @Override
    public List<Booking> getHistory(long id, int page) {
        return bookingDao.getClientHistory(id, Pagination.BOOKINGS_PAGE_SIZE * page);
    }

    @Override
    public int getTotalHistoryCount(long id) {
        return bookingDao.getClientHistoryCount(id);
    }

    @Override
    public int getTotalBookingCount(long id) {
        return bookingDao.getClientBookingCount(id);
    }

    @Override
    public void setBookingRatingAndReview(long bookingId, int rating, String review) {
        bookingDao.setRatingAndReview(bookingId, rating, review);
    }
}
