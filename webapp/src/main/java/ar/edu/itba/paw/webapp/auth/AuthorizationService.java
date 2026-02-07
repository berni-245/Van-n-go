package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.exceptions.UnauthorizedException;
import ar.edu.itba.paw.exceptions.UserIdentityMismatchException;
import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
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

        // TODO find booking by driver_id and client_id to do this less ugly
        long count = 0;
        Client c = clientService.findById(clientId);
        Driver d = driverService.findById(driverId);
        count += clientService.getBookings(c, BookingState.PENDING, 1).stream().filter(b -> b.getDriver().equals(d)).count();
        count += clientService.getBookings(c, BookingState.ACCEPTED, 1).stream().filter(b -> b.getDriver().equals(d)).count();
        count += clientService.getBookings(c, BookingState.REJECTED, 1).stream().filter(b -> b.getDriver().equals(d)).count();
        count += clientService.getBookings(c, BookingState.FINISHED, 1).stream().filter(b -> b.getDriver().equals(d)).count();
        count += clientService.getBookings(c, BookingState.CANCELED, 1).stream().filter(b -> b.getDriver().equals(d)).count();
        if (count <= 0)
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