package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public abstract class UserJpaDao<T extends User> implements UserDao<T> {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public boolean mailExists(String mail) {
        final TypedQuery<User> query = em.createQuery(
                "from User as u where u.mail = :mail",
                User.class
        );
        query.setParameter("mail", mail);
        return !query.getResultList().isEmpty();
    }

    public abstract Optional<T> findById(long id);

    @Override
    public void editProfile(User user, String username, String mail) {
        user.setUsername(username);
        user.setMail(mail);
        em.merge(user);
    }

    @Override
    public void updatePassword(User user, String updatedPassword) {
        user.setPassword(updatedPassword);
        em.merge(user);
    }
}
