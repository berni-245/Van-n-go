package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.PasswordTokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordTokenServiceImpl implements PasswordTokenService {

    private final static int MAX_TOKEN_LENGTH = 6;
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordTokenServiceImpl.class);

    private final PasswordTokenDao passwordTokenDao;
    private final GeneralUserService generalUserService;
    private final MailService mailService;
    private final ClientService clientService;
    private final DriverService driverService;


    @Autowired
    public PasswordTokenServiceImpl(PasswordTokenDao passwordTokenDao, GeneralUserService generalUserService, MailService mailService,
                                    ClientService clientService, DriverService driverService) {
        this.passwordTokenDao = passwordTokenDao;
        this.generalUserService = generalUserService;
        this.mailService = mailService;
        this.clientService = clientService;
        this.driverService = driverService;
    }

    @Transactional
    @Override
    public void createPasswordToken(String mail) {
        Optional<User> posibleUser = generalUserService.findByMail(mail);
        if (posibleUser.isPresent()) {
            User user = posibleUser.get();
            String token = generateToken();
            passwordTokenDao.createToken(user, token);
            mailService.sendPasswordToken(mail, user.getUsername(), token, user.getLanguage().getLocale());
        }
    }

    @Override
    public boolean isValidUserToken(User user, String token) {
        return passwordTokenDao.getTokenByUser(user).filter(dbToken -> dbToken.getToken().equals(token)
                && dbToken.getExpiryDate().isAfter(LocalDateTime.now())).isPresent();
    }

    @Override
    @Transactional
    public void deleteUserToken(User user) {
        passwordTokenDao.deleteTokensByUser(user);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString().substring(0, MAX_TOKEN_LENGTH);
    }

    @Override
    @Transactional
    public boolean UpdatePassword(String mail, String password, String token){
        Optional<User> posibleUser = generalUserService.findByMail(mail);
        if(posibleUser.isEmpty()) return false;
        User user = posibleUser.get();
        if(isValidUserToken(user, token)){
            getServiceForUser(user).updatePassword(user, password);
            deleteUserToken(user);
            LOGGER.info("Password updated for user {}", user.getUsername());
            return true;
        }
        LOGGER.info("Invalid token for user {}", user.getUsername());
        return false;
    }

    private <T extends User> UserService<T> getServiceForUser(T user) {
        if (user.isDriver())
            return (UserService<T>) driverService;
        else
            return (UserService<T>) clientService;
    }




}
