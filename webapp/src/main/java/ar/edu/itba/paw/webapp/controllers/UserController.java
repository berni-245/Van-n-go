package ar.edu.itba.paw.webapp.controllers;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

// curl -H'Content-type:application/json' http://localhost:8080/users -v
// No sé porque no funca sigo mañana, me tira 302 moved a login así que sospecho que me faltó quitar algo

@Path("/users")
@Component
public class UserController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController() {
        // add services needed here
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getMethod() {
        List<String> list = List.of("Hola", "jaja!", "prueba");

        return Response.ok(new GenericEntity<List<String>>(list) {}).build();
    }
}
