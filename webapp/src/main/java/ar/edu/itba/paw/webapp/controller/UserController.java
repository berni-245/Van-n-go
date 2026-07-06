package ar.edu.itba.paw.webapp.controller;




import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.dto.user.*;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/api/users")
@Component
public class UserController {
    @Context
    private UriInfo uriInfo;

    @Autowired
    private PasswordTokenService passwordTokenService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DriverService driverService;


    @POST
    @Path("/token")
    @Consumes(value = {CustomMediaType.MAIL})
    public Response createPasswordToken(@Valid final MailDTO mailDTO) {
        passwordTokenService.createPasswordToken(mailDTO.getMail());
        return Response.ok().build();
    }

    @PATCH
    @Path("/password")
    @Consumes(value = {CustomMediaType.PASSWORD_TOKEN})
    public Response validatePasswordToken(@Valid final RecoverPasswordDTO recoverPasswordDTO){
        if(passwordTokenService.UpdatePassword(recoverPasswordDTO.getMail(), recoverPasswordDTO.getPassword(), recoverPasswordDTO.getToken())){
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // fijarse de hacer otro PATCH a /password pero con el otro mediatype y la anotation de auth (fijarse si content negociation es valido en este caso paara rest)
    // despues intentar sacar el general User Service y ver de quedarme con client y driver para consistencia
    // agregar al UserDao el findByMail

    @PATCH
    @Path("/password")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes(value = {CustomMediaType.PASSWORD_CHANGE})
    public Response updateUserPassword(@QueryParam("id") final int id, @Valid final UpdateUserPasswordDTO dto) {
        User user = getAuthUser();
        UserService<User> userService = getServiceForUser(user);
        if (dto.getPassword() != null && dto.getOldPassword() != null) {
            userService.updatePassword(user, dto.getPassword());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @SuppressWarnings("unchecked")
    private <T extends User> T getAuthUser() {
        PawUserDetails pawUserDetails = (PawUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = pawUserDetails.getUser();
        return (T) user;
    }

    @SuppressWarnings("unchecked")
    private <T extends User> UserService<T> getServiceForUser(T user) {
        if (user.isDriver())
            return (UserService<T>) driverService;
        else
            return (UserService<T>) clientService;
    }

}
