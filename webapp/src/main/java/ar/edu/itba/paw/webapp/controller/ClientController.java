package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.webapp.dto.ClientDTO;
import ar.edu.itba.paw.webapp.dto.CreateClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response createClient(final CreateClientDTO clientDTO) {
        Client client = clientService.create(clientDTO.getUsername(), clientDTO.getMail(), clientDTO.getPassword(),clientDTO.getZoneId() , LocaleContextHolder.getLocale());
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(client.getId())).build()).entity(ClientDTO.fromClient(uriInfo,client)).build();

    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getClientById(@PathParam("id") final int id) {
        //TODO: add 401 and 404 responses
        Client client = clientService.findById(id);
        return Response.ok(ClientDTO.fromClient(uriInfo, client)).build();
    }
}
