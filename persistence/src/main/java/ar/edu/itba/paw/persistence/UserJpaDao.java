package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

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
}
