package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    @Autowired
    public UserServiceImpl(UserDao userDao, MailService mailService, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    public User create(String username, String mail, String password) {
        mailService.sendClientWelcomeMail(mail, username);
        return userDao.create(username, mail, passwordEncoder.encode(password));
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
    public boolean usernameExists(String username) {
        return userDao.usernameExists(username);
    }
}
