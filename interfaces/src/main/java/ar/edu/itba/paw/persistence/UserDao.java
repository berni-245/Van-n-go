package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    User create(String username, String mail);

    Optional<User> findById(long id);
}
