package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.exceptions.UnauthorizedException;
import ar.edu.itba.paw.exceptions.UserIdentityMismatchException;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// This class is for method security, more info in:
// https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#migration-enableglobalmethodsecurity

@Component("authService")
public class AuthorizationService {
    @Autowired
    ClientService clientService;

    @Autowired
    DriverService driverService;

    @Autowired
    MessageService messageService;

    public boolean isAuthUser(Integer userId) {
        return isAuthUserEither(userId, userId);
    }

    public boolean isAuthUserEither(Integer userId1, Integer userId2) {
        PawUserDetails pawUser = getCurrentUser();

        if (pawUser.getUser().getId().equals(userId1))
            return true;

        if (pawUser.getUser().getId().equals(userId2))
            return true;

        throw new UserIdentityMismatchException();
    }

    public boolean isAuthDriverInBookingWith(int clientId) {
        User user = getCurrentUser().getUser();
        if (!user.isDriver())
            throw new ForbiddenClientInfoAccessException();
        if (!driverService.existsBookingBetween(clientId, user.getId()))
            throw new ForbiddenClientInfoAccessException();
        return true;
    }

    public boolean isAuthUserOrDriverInBookingWith(int clientId) {
        User user = getCurrentUser().getUser();
        if (user.isDriver() && driverService.existsBookingBetween(clientId, user.getId()))
            return true;
        return isAuthUser(clientId);
    }

    public boolean isAuthUserInConversation(int messageId) {
        Message m = messageService.getMessageById(messageId);
        int authUserId = getCurrentUser().getUser().getId();
        if (authUserId != m.getClient().getId() && authUserId != m.getDriver().getId()) {
            throw new ForbiddenConversationException();
        }
        return true;
    }

    public boolean isValidConversation(int driverId, int clientId) {
        isAuthUserEither(driverId, clientId);

        if (!clientService.existsBookingBetween(clientId, driverId))
            throw new ForbiddenConversationException();
        return true;
    }

    private PawUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException();
        }

        if (!(auth.getPrincipal() instanceof PawUserDetails pawUser)) {
            throw new UnauthorizedException();
        }
        return pawUser;
    }
}