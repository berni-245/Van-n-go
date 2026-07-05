package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.services.PasswordTokenService;
import ar.edu.itba.paw.webapp.dto.user.MailDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
    @Consumes(value = {CustomMediaType.MAIL})
    public Response createPasswordToken(@Valid final MailDTO mailDTO) {
        passwordTokenService.createPasswordToken(mailDTO.getMail());
        return Response.ok().build();
    }


}
