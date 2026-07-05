package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.PasswordToken;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PasswordTokenJpaDao implements PasswordTokenDao {

    @PersistenceContext
    private EntityManager em;

    private static final int EXPIRATION_TIME_MINUTES = 60; // 1 hour

    @Override
    public void createToken(User user, String token) {
        deleteTokensByUser(user); // Ensure only one token per user
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_TIME_MINUTES);
        PasswordToken passwordToken = new PasswordToken(user, token, expiryDate);
        em.persist(passwordToken);
    }

    @Override
    public void deleteTokensByUser(User user) { //TODO: testear pedir varios tokens y que solo funcione el ultimo generado. Si no funciona ver em.flush()
        em.createQuery("DELETE FROM PasswordToken pt WHERE pt.user = :user")
                .setParameter("user", user)
                .executeUpdate();
    }

    @Override
    public Optional<PasswordToken> getTokenByUser(User user) {
        TypedQuery<PasswordToken> query = em.createQuery(
                "SELECT pt FROM PasswordToken pt WHERE pt.user = :user AND pt.expiryDate > :now", PasswordToken.class);
        query.setParameter("user", user);
        query.setParameter("now", LocalDateTime.now());
        return query.getResultList().stream().findFirst();
    }

    @Override
    public void deleteToken(PasswordToken token) {
        em.remove(token);
    }


}
