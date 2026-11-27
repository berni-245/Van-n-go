package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Zone;

public interface ClientDao extends UserDao<Client> {
    Client create(String username, String mail, String password, Zone zone, Language language);

    void editProfile(Client client, String username, String mail, Zone zone, Language language);
}
