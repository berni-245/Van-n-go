package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao<T extends User> {
    boolean mailExists(String mail);

    Optional<T> findById(long id);

    Optional<T> findByUsername(String username);

    void editProfile(User user, String username, String mail);

    void updatePassword(User user, String updatedPassword);
}
