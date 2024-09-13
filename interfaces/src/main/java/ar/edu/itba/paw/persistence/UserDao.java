package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);

    User create(String username, String mail, String password);

    User findByUsername(String username);

    boolean mailExists(String mail);

    boolean isDriver(String username);
}
