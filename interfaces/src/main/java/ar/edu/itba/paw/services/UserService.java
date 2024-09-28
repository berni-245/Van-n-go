package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {
    boolean mailExists(String mail);

    boolean usernameExists(String mail);

    Optional<? extends User> findByUsername(String username);
}
