package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJpaDao implements UserDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean mailExists(String mail) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.mail = :mail", User.class);
        query.setParameter("mail", mail);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public Optional<? extends User> findByUsername(String username) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.username = :username", User.class);
        query.setParameter("username", username);
        final List<User> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst());
    }

    @Override
    public int updateMail(long userId, String updatedMail) {
        User user = findById(userId);
        if(user != null) {
            user.setMail(updatedMail);
            em.persist(user);
        }
        return 0;  //TODO posiblemente deberiamos cambiarlo a void
    }

    @Override
    public int updatePassword(long userId, String updatedPassword) {
        User user = findById(userId);
        if(user != null) {
            user.setPassword(updatedPassword);
            em.persist(user);
        }
        return 0;
    }

    @Override
    public int updateUsername(long userId, String updatedUsername) {
        User user = findById(userId);
        if(user != null) {
            user.setUsername(updatedUsername);
            em.persist(user);
        }
        return 0;
    }

    private User findById(long id) {
        return em.find(User.class, id);
    }
}
