package ar.edu.itba.paw.webapp.controller;



import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.dto.user.MailDTO;
import ar.edu.itba.paw.webapp.dto.user.RecoverPasswordDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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



}
