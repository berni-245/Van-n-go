package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.ClientDao;
import ar.edu.itba.paw.persistence.DriverDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserDao userDao;

    @Autowired
    protected final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            MailService mailService
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    protected long createUser(String username, String mail, String password) {
        return userDao.create(username, mail, passwordEncoder.encode(password));
    }

    @Override
    public Optional<? extends User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public boolean mailExists(String mail) {
        return userDao.mailExists(mail);
    }

    @Override
    public boolean usernameExists(String username) {
        return userDao.usernameExists(username);
    }
}