package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.PawUserDetails;
import ar.edu.itba.paw.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ValidatorWithLoggedUser {
    private final User user;

    ValidatorWithLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof PawUserDetails pud) {
            this.user = pud.getUser();
        } else {
            // TODO: Make custom exception.
            throw new RuntimeException("Idk unauthorized?");
        }
    }

    public User getUser() {
        return user;
    }
}
