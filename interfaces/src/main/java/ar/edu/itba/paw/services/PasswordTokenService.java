package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

public interface PasswordTokenService {
    void createPasswordToken(String mail);

    boolean isValidUserToken(User mail, String token);

    void deleteUserToken(User mail);

    boolean UpdatePassword(String mail, String password, String token);

}
