package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Zone;

import java.util.Optional;

public interface ClientDao {
    Client create(String username, String mail, String password) ;

    Optional<Client> findById(long id);

    Optional<Client> findByUsername(String username);

    void editProfile(Client client, String username, String mail, Zone zone);
}
