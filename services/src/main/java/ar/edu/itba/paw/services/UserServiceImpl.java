package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidUserOnBookingCancelException;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class UserServiceImpl<T extends User> implements UserService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao<T> userDao;

    protected final MailService mailService;

    protected final PasswordEncoder passwordEncoder;

    protected final BookingDao bookingDao;

    @Autowired
    public UserServiceImpl(
            UserDao<T> userDao,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            BookingDao bookingDao
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.bookingDao = bookingDao;
    }

//    protected long createUser(String username, String mail, String password) {
//        return 1;//userDao.create(username, mail, passwordEncoder.encode(password));
//    }

    @Transactional
    @Override
    public boolean usernameExists(String username) {
        return userDao.findByUsername(username).isPresent();
    }

    @Transactional
    @Override
    public boolean mailExists(String mail) {
        return userDao.mailExists(mail);
    }

    @Transactional
    @Override
    public void editProfile(User user, String username, String mail) {
        userDao.editProfile(user, username, mail);
    }

    @Transactional
    @Override
    public void updatePassword(User user, String password) {
        userDao.updatePassword(user, passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public Booking cancelBooking(long bookingId, T user) {
        Booking booking = bookingDao.getBookingById(bookingId).orElseThrow();
        User bookingUser = user.isDriver() ? booking.getDriver() : booking.getClient();
        if (!bookingUser.equals(user)) throw new InvalidUserOnBookingCancelException();
        bookingDao.cancelBooking(booking);
        LOGGER.info("{} canceled booking {}", user.getUsername(), bookingId);
        return booking;
    }
}