package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.user.ClientDTO;
import ar.edu.itba.paw.webapp.dto.user.CreateClientDTO;
import ar.edu.itba.paw.webapp.dto.user.UpdateClientDTO;
import ar.edu.itba.paw.webapp.utils.CacheControlUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import ar.edu.itba.paw.webapp.validation.ValidImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

@Path(UriUtils.CLIENTS_URL)
@Component
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ImageService imageService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = {CustomMediaType.CLIENT_CREATE})
    @Produces(value = {CustomMediaType.CLIENT})
    public Response createClient(@HeaderParam("Accept-Language") @DefaultValue("es-AR") String lang, @Valid final CreateClientDTO clientDTO) {
        Client client = clientService.create(clientDTO.getUsername(), clientDTO.getEmail(), clientDTO.getPassword(), clientDTO.getZoneId(), Locale.forLanguageTag(lang));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(client.getId())).build()).entity(ClientDTO.fromClient(uriInfo,client)).build();
    }

    @GET
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUserOrDriverInBookingWith(#id)")
    @Produces(value = {CustomMediaType.PUBLIC_CLIENT})
    public Response getPublicClientInfoById(@PathParam(UriUtils.ID_PARAM) final int id) {
        Client client = clientService.findById(id);
        return Response.ok(ClientDTO.fromPublicClientInfo(uriInfo, client)).build();
    }

    @GET
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Produces(value = {CustomMediaType.CLIENT})
    public Response getClientById(@PathParam(UriUtils.ID_PARAM) final int id) {
        Client client = clientService.findById(id);
        return Response.ok(ClientDTO.fromClient(uriInfo, client)).build();
    }


    @PATCH
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes(value = {CustomMediaType.CLIENT_UPDATE})
    @Produces(value = {CustomMediaType.CLIENT})
    public Response updateClient(@PathParam(UriUtils.ID_PARAM) final int id, @Valid final UpdateClientDTO dto) {
        Client c = clientService.findById(id);
        clientService.editProfile(
                c, dto.getUsernameOr(c.getUsername()), dto.getEmailOr(c.getMail()),
                dto.getZoneIdOr(c.getZone().getId()), dto.getPreferredLanguageOr(c.getLanguage().name()));
        return Response.ok(ClientDTO.fromClient(uriInfo, c)).build();
    }

    @GET
    @Path("/{clientId}/profile-picture")
    @PreAuthorize("@authService.isAuthUserOrDriverInBookingWith(#id)")
    @Produces({MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Response getProfilePicture(@PathParam(UriUtils.CLIENT_ID_PARAM) final int id, @Context Request request) {
        Client client = clientService.findById(id);
        Integer imageId = client.getPfp();
        if(imageId != null){
            Image picture = imageService.getImage(imageId);
            if (picture.getData() != null) {
                return CacheControlUtils.setEtagToResponse(Response.ok(picture.getData()).type(picture.getType()), request, picture.hashCode());
            }
        }
        return Response.status(Response.Status.NOT_FOUND).type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(new ImageNotFoundException())).build();
    }

    @PUT
    @Path("/{id}/profile-picture")
    @PreAuthorize("@authService.isAuthUser(#id)")
    @Consumes({MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Response uploadProfilePicture(@PathParam(UriUtils.ID_PARAM) final int id, @ValidImage byte[] imageData,
                                         @HeaderParam("Content-Type") String type) {
        Client client = clientService.findById(id);
        imageService.uploadPfp(client, imageData, "client_" + client.getId() + "_pfp", type);
        return Response.noContent().build();
    }


}
