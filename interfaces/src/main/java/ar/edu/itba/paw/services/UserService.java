package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(long id);

    User create(String username, String mail, String password);

    boolean isDriver(String username);

    boolean mailExists(String mail);

    User findByUsername(String username);
}
