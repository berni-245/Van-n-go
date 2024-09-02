package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    public UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    public User create(String username, String mail) {
        // Register user
        // TODO
        // 1. Validate inputs
        // 2. Store in database
        // 3. Generate validation token and store in db
        // 4. Send validation token in a welcome email
        // 5. Add user to manual verification queue
        // 6. ... Whatever else the app needs
        return userDao.create(username, mail);
    }
}
