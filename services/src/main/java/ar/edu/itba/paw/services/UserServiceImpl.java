package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ForbiddenClientCancelBookingException;
import ar.edu.itba.paw.exceptions.ForbiddenDriverCancelBookingException;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Language;
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

    @Transactional
    @Override
    public T findById(int id) {
        return userDao.findById(id);
    }

    @Transactional
    @Override
    public boolean usernameExists(String username) {
        return userDao.usernameExists(username);
    }

    @Transactional
    @Override
    public boolean mailExists(String mail) {
        return userDao.mailExists(mail);
    }

    @Transactional
    @Override
    public void editProfile(User user, String username, String mail, Language language) {
        userDao.editProfile(user, username, mail, language);
    }

    @Transactional
    @Override
    public void updatePassword(User user, String password) {
        userDao.updatePassword(user, passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public Booking cancelBooking(T user, int bookingId) {
        Booking booking = getBookingById(user, bookingId);
        if (!booking.getState().equals(BookingState.ACCEPTED)) {
            if (user.isDriver()) throw new ForbiddenDriverCancelBookingException();
            else throw new ForbiddenClientCancelBookingException();
        }
        bookingDao.cancelBooking(booking);
        LOGGER.info("{} canceled booking {}", user.getUsername(), bookingId);
        return booking;
    }
}