package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public abstract class UserJpaDao<T extends User> implements UserDao<T> {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public void editProfile(User user, String username, String mail, Language language) {
        user.setUsername(username);
        user.setMail(mail);
        user.setLanguage(language);
        em.merge(user);
    }

    @Override
    public void updatePassword(User user, String updatedPassword) {
        user.setPassword(updatedPassword);
        em.merge(user);
    }

    @Override
    public boolean mailExists(String mail) {
        final TypedQuery<User> query = em.createQuery(
                "from User as u where u.mail = :mail",
                User.class
        );
        query.setParameter("mail", mail);
        return !query.getResultList().isEmpty();
    }

    @Override
    public boolean usernameExists(String username) {
        final TypedQuery<User> query = em.createQuery(
                "from User as u where u.username = :username",
                User.class
        );
        query.setParameter("username", username);
        return !query.getResultList().isEmpty();
    }
}
