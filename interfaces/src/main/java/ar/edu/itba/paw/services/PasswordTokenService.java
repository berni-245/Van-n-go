package ar.edu.itba.paw.services;

public interface PasswordTokenService {
    void createPasswordToken(String mail);

    boolean isValidUserToken(String mail, String token);

    void deleteUserToken(String mail);

}
