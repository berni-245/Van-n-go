package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.PasswordTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordTokenImpl implements PasswordTokenService {

    private final static int MAX_TOKEN_LENGTH = 6;

    private final PasswordTokenDao passwordTokenDao;
    private final GeneralUserService generalUserService;
    private final MailService mailService;


    @Autowired
    public PasswordTokenImpl(PasswordTokenDao passwordTokenDao, GeneralUserService generalUserService, MailService mailService) {
        this.passwordTokenDao = passwordTokenDao;
        this.generalUserService = generalUserService;
        this.mailService = mailService;
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
    public boolean isValidUserToken(String user, String token) { //TODO: terminar
        return false;
    }

    @Override
    public void deleteUserToken(String user) {

    }

    private static String generateToken() {
        return UUID.randomUUID().toString().substring(0, MAX_TOKEN_LENGTH);
    }





}
