package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.user.ClientDTO;
import ar.edu.itba.paw.webapp.dto.user.CreateClientDTO;
import ar.edu.itba.paw.webapp.dto.user.UpdateClientDTO;
import ar.edu.itba.paw.webapp.validation.ValidImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

@Path("/api/clients")
@Component
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ImageService imageService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response createClient(@HeaderParam("Accept-Language") @DefaultValue("es-AR") String lang, @Valid final CreateClientDTO clientDTO) {
        Client client = clientService.create(clientDTO.getUsername(), clientDTO.getEmail(), clientDTO.getPassword(), clientDTO.getZoneId(), Locale.forLanguageTag(lang));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(client.getId())).build()).entity(ClientDTO.fromClient(uriInfo,client)).build();
    }

    @GET
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getClientById(@PathParam("id") final int id) {
        Client client = clientService.findById(id);
        return Response.ok(ClientDTO.fromClient(uriInfo, client)).build();
    }


    @PATCH
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response updateClient(@PathParam("id") final int id, @Valid final UpdateClientDTO dto) {
        Client c = clientService.findById(id);
        clientService.editProfile(
                c, dto.getUsernameOr(c.getUsername()), dto.getEmailOr(c.getMail()),
                dto.getZoneIdOr(c.getZone().getId()), dto.getPreferredLanguageOr(c.getLanguage().name()));
        if (dto.getPassword() != null && dto.getOldPassword() != null) {
            clientService.updatePassword(c, dto.getPassword());
        }
        return Response.ok(ClientDTO.fromClient(uriInfo, c)).build();
    }

    @GET
    @Path("/{clientId}/profile-picture")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Produces("image/*")
    public Response getProfilePicture(@PathParam("clientId") final int id) {
        //TODO: add 401 response
        Client client = clientService.findById(id);
        Integer imageId = client.getPfp();
        if(imageId != null){
            byte[] pictureData = imageService.getImage(imageId).getData();
            if (pictureData != null) {
                return Response.ok(pictureData).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/profile-picture")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes({"image/jpeg", "image/png"})
    public Response uploadProfilePicture(@PathParam("id") final int id, @ValidImage byte[] imageData) {
        Client client = clientService.findById(id);
        imageService.uploadPfp(client, imageData, "client_" + client.getId() + "_pfp");
        return Response.created(uriInfo.getAbsolutePathBuilder().build()).build();
    }


}
