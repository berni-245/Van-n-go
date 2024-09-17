package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{
    @Autowired
    private UserService userService;

    @Autowired
    private ClientDao clientDao;

    @Override
    public Client create(String username, String mail, String password) {
        User user = userService.create(username, mail, password);
        return clientDao.create(user);
    }

    @Override
    public Optional<Client> findById(long id) {
        return clientDao.findById(id);
    }
}
