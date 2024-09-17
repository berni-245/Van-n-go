package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;

import java.util.Optional;

public interface ClientService {
    Client create(String username, String mail, String password);

    Optional<Client> findById(long id);
}
