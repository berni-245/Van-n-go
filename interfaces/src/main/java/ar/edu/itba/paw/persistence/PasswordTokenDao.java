package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.PasswordToken;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface PasswordTokenDao {
    void createToken(User user, String token);

    void deleteTokensByUser(User user);

    Optional<PasswordToken> getTokenByUser(User user);

    void deleteToken(PasswordToken token);

    void deleteExpiredTokens();
}
