package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientJpaDao implements ClientDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Client create(String username, String mail, String password) {
        Client client = new Client(username,mail,password);
        em.persist(client);
        return client;
    }

    @Override
    public Optional<Client> findById(long id) { //TODO: revisar si podemos omitir el optional
        return Optional.ofNullable(em.find(Client.class, id));
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        final TypedQuery<Client> query = em.createQuery("from Client as c where c.username = :username", Client.class); //NO deberia marcar error
        query.setParameter("username", username);
        final List<Client> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst());
    }

    @Transactional
    @Override
    public void editProfile(Client client, String username, String mail) {
        client.setUsername(username);
        client.setMail(mail);
        em.merge(client);
    }
}
