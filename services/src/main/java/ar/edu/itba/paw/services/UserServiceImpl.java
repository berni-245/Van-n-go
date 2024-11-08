package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    protected final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
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
        return 1;//userDao.create(username, mail, passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public Optional<? extends User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public boolean mailExists(String mail) {
        return userDao.mailExists(mail);
    }

    @Transactional
    @Override
    public boolean usernameExists(String username) {
        return userDao.usernameExists(username);
    }

    @Transactional
    @Override
    public int updateMail(long userId, String updatedMail) {
        return userDao.updateMail(userId, updatedMail);
    }

    @Transactional
    @Override
    public void updatePassword(long userId, String updatedPassword) {
        userDao.updatePassword(userId, passwordEncoder.encode(updatedPassword));
    }

    @Transactional
    @Override
    public int updateUsername(long userId, String updatedUsername){
        return userDao.updateUsername(userId,updatedUsername);
    }

    protected Language getLanguageFromLocale(Locale locale) {
        if (locale.getLanguage().equals("en")) {
            return Language.ENGLISH;
        }
        return Language.SPANISH;
    }

}