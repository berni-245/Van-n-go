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

    public User create(String username) {
        // register user
        // TODO
        // 1. validar inputs
        // 2. ingresarlo en base de datos
        // 3. generar un token de validación y guardarlo en base
        // 4. enviar el token de validación en un correo de bienvenida
        // 5. agregar al usuario a una cola de verificación manual...
        // 6. ... sigue tan complejo como lo requiera la aplicación
        return userDao.create(username);
    }
}
