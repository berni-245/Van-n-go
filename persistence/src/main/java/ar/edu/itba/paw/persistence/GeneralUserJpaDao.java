package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class GeneralUserJpaDao implements GeneralUserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findByMail(String mail) {
        final TypedQuery<User> query = em.createQuery(
                "from User as u where u.mail = :mail",
                User.class
        );
        query.setParameter("mail", mail);
        final List<User> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

}
