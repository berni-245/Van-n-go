package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService<T extends User> extends UserBookingService<T> {
    boolean mailExists(String mail);

    boolean usernameExists(String mail);

    Optional<T> findByUsername(String username);

    Optional<T> findById(long id);

    void editProfile(User user, String username, String mail);

    void updatePassword(User user, String password);
}
