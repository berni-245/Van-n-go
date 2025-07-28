package ar.edu.itba.paw.webapp.controllers;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;

// curl -H'Content-type:application/json' http://localhost:8080/api/users -v

@Path("/api/users") // TODO agregar un utils con los base url /api o algo
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

        return Response.ok(new GenericEntity<>(list) {}).build();
    }

    @GET
    @Path("/test")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response testMapper() { // to test the Exception to Response mapper
        throw new UserNotFoundException();
    }
}
