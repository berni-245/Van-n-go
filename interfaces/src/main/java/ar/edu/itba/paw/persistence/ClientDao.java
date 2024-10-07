package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;

import java.util.Optional;

public interface ClientDao {
    Client create(long id);

    Optional<Client> findById(long id);

    Optional<Client> findByUsername(String username);
}
