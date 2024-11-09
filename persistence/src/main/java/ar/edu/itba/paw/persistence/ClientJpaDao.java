package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Zone;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientJpaDao extends UserJpaDao<Client> implements ClientDao {

    @Override
    public Client create(String username, String mail, String password, Language language) {
        Client client = new Client(username, mail, password, language);
        em.persist(client);
        return client;
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        final TypedQuery<Client> query = em.createQuery(
                "from Client as c where c.username = :username",
                Client.class
        );
        query.setParameter("username", username);
        final List<Client> list = query.getResultList();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.getFirst());
    }

    @Override
    public Optional<Client> findById(int id) { //TODO: revisar si podemos omitir el optional
        return Optional.ofNullable(em.find(Client.class, id));
    }

    @Override
    public void editProfile(Client client, String username, String mail, Zone zone, Language language) {
        super.editProfile(client, username, mail);
        client.setZone(zone);
        client.setLanguage(language);
        em.merge(client);
    }
}
