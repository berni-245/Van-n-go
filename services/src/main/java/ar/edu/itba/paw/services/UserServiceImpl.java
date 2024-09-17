package ar.edu.itba.paw.services;
import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private BookingDao bookingDao;

    public UserServiceImpl(final UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    public User create(String username,String mail, String password) {
        mailService.sendClientWelcomeMail(mail,username);
        // 3. generar un token de validación y guardarlo en base
        // 4. enviar el token de validación en un correo de bienvenida
        // 5. agregar al usuario a una cola de verificación manual...
        // 6. ... sigue tan complejo como lo requiera la aplicación
        return userDao.create(username,mail,passwordEncoder.encode(password));
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public boolean isDriver(String username) {
        return userDao.isDriver(username);
    }

    @Override
    public boolean mailExists(String mail) {
        return userDao.mailExists(mail);
    }

    @Override
    public Optional<Booking> appointBooking(long driverId, long clientId, Date date) {
        return bookingDao.appointBooking(driverId, clientId, date);
    }
}