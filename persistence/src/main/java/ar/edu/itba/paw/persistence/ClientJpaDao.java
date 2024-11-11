package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.Zone;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.LocalDate;
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
    public Client findById(int id) {
        Client c = em.find(Client.class, id);
        if (c == null) throw new UserNotFoundException();
        return c;
    }

    @Override
    public void editProfile(Client client, String username, String mail, Zone zone, Language language) {
        super.editProfile(client, username, mail,language);
        client.setZone(zone);
        em.merge(client);
    }

    @Override
    public boolean mailExists(String mail) {
        final TypedQuery<Client> query = em.createQuery(
                "from Client as c where c.mail = :mail",
                Client.class
        );
        query.setParameter("mail", mail);
        return !query.getResultList().isEmpty();
    }
}
