package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl extends UserServiceImpl implements ClientService {
    @Autowired
    private ClientDao clientDao;

    @Autowired
    private BookingDao bookingDao;

    public ClientServiceImpl(
            UserDao userDao,
            ClientDao clientDao,
            PasswordEncoder passwordEncoder,
            MailService mailService
    ) {
        super(userDao, passwordEncoder, mailService);
        this.clientDao = clientDao;
    }

    //@Transactional
    @Override
    public Client create(String username, String mail, String password) {
        long id = createUser(username, mail, password);
        // Client instance will be created with unencrypted password.
        // Is that a problem tho?
        Client client = clientDao.create(id, username, mail, password);
        mailService.sendClientWelcomeMail(mail, username);
        return client;
    }

    @Override
    public Optional<Client> findById(long id) {
        return clientDao.findById(id);
    }

    @Override
    public Optional<Booking> appointBooking(long driverId, long clientId, LocalDate date) {
        return bookingDao.appointBooking(driverId, clientId, date);
    }

    @Override
    public List<Booking> getBookings(long id) {
        return bookingDao.getClientBookings(id);
    }

    @Override
    public List<Booking> getHistory(long id) {
        return bookingDao.getClientHistory(id);
    }

    @Override
    public void setRating(long bookingId, int rating) {
        bookingDao.setRating(bookingId, rating);
    }
}
