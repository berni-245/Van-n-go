package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.ClientDTO;
import ar.edu.itba.paw.webapp.dto.CreateClientDTO;
import ar.edu.itba.paw.webapp.dto.UpdateClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response createClient(final CreateClientDTO clientDTO) {
        Client client = clientService.create(clientDTO.getUsername(), clientDTO.getEmail(), clientDTO.getPassword(), clientDTO.getZoneId(), LocaleContextHolder.getLocale());
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


    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response updateClient(@PathParam("id") final int id, final UpdateClientDTO dto) {
        //TODO: add 401 , 403 responses
        Client c = clientService.findById(id);
        clientService.editProfile(
                c, dto.getUsernameOr(c.getUsername()), dto.getEmailOr(c.getMail()),
                dto.getZoneIdOr(c.getZone().getId()), dto.getPreferredLanguageOr(c.getLanguage().name()));
        // TODO move this bellow to validator
        String newPass = dto.getPassword();
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$";
        boolean oldPassCheck = passwordEncoder.matches(dto.getOldPassword(), c.getPassword());
        if (newPass != null && newPass.matches(regex) && oldPassCheck)
            clientService.updatePassword(c, newPass);
        return Response.ok(ClientDTO.fromClient(uriInfo, c)).build();
    }

    @GET
    @Path("/{clientId}/profile-picture")
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
    @Consumes({"image/jpeg", "image/png"})
    public Response uploadProfilePicture(@PathParam("id") final int id, byte[] imageData) {
        //TODO: add 401 and 403 responses
        Client client = clientService.findById(id);
        imageService.uploadPfp(client, imageData, "client_" + client.getId() + "_pfp");
        return Response.created(uriInfo.getAbsolutePathBuilder().build()).build();
    }


}
