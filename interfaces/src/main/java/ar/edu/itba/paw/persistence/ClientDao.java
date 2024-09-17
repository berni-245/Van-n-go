package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface ClientDao {
    Client create(User user);

    Optional<Client> findById(long id);
}
