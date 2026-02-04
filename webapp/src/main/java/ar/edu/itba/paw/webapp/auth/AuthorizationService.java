package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.UnauthorizedException;
import ar.edu.itba.paw.exceptions.UserIdentityMismatchException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// This class is for method security, more info in:
// https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#migration-enableglobalmethodsecurity

@Component("authService")
public class AuthorizationService {
    public boolean isAuthenticatedUser(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        PawUserDetails principal = (PawUserDetails) auth.getPrincipal();
        if (principal.getUser().getId() != id) {
            throw new UserIdentityMismatchException();
        }

        return true;
    }
}
