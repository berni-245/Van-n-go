package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {
    boolean mailExists(String mail);

    boolean usernameExists(String mail);

    Optional<? extends User> findByUsername(String username);

    int updateMail(long userId, String updatedMail);

    int updatePassword(long userId, String updatedPassword);

    int updateUsername(long userId, String updatedUsername);
}
