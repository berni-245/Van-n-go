package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService<T extends User> extends UserBookingService<T> {
    boolean mailExists(String mail);

    boolean usernameExists(String mail);

    T findById(int id);

    Optional<T> findByUsername(String username);

    void editProfile(User user, String username, String mail, Language language);

    void updatePassword(User user, String password);
}
