package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao<T extends User> {
    boolean mailExists(String mail);

    T findById(int id);

    Optional<T> findByUsername(String username);

    void editProfile(User user, String username, String mail, Language language);

    void updatePassword(User user, String updatedPassword);
}
