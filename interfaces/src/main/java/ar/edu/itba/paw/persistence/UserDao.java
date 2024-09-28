package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    long create(String username, String mail, String password);

    boolean mailExists(String mail);

    boolean usernameExists(String mail);

    Optional<? extends User> findByUsername(String username);
}
