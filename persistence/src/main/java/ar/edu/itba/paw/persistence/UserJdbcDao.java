package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao{
    @Override
    public Optional<User> findById(long id) {
        return Optional.of(new User("PAW from persistence"));
    }
}
