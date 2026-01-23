package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.webapp.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/api/clients")
@Component
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getClientById(@PathParam("id") final int id){
        Client client = clientService.findById(id);
        return Response.ok(ClientDTO.fromClient(uriInfo,client)).build();
    }
}
