package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ValidatorWithLoggedUser {
    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof PawUserDetails pud) {
            return pud.getUser();
        }
        return null;
    }
}
