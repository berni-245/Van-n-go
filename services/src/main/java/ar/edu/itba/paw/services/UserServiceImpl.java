package ar.edu.itba.paw.services;

import java.util.Optional;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Override
    public Optional<User> findById(long id) {
        return Optional.of(new User("PAW"));
    }
}
