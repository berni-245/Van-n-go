package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.models.Pagination;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.MessageService;
import ar.edu.itba.paw.webapp.auth.PawUserDetails;
import ar.edu.itba.paw.webapp.dto.message.CreateMessageDTO;
import ar.edu.itba.paw.webapp.dto.message.MessageDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/messages")
@Component
public class MessageController {
    @Context
    private UriInfo uriInfo;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DriverService driverService;

    @GET
    @PreAuthorize("@authService.isValidConversation(#driverId, #clientId)")
    @Produces(value = {CustomMediaType.MESSAGE_LIST})
    public Response listMessages(
            @QueryParam("driverId") Integer driverId,
            @QueryParam("clientId") Integer clientId,
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        Client client = clientService.findById(clientId);
        Driver driver = driverService.findById(driverId);
        int maxPages = messageService.getConversationPages(client, driver);
        Pagination.validatePage(page, maxPages);
        List<MessageDTO> messages = messageService.getConversationWithoutBooking(client, driver, page).stream()
                .map(MessageDTO.mapper(uriInfo))
                .toList();
        Response.ResponseBuilder response = Response.ok(new GenericEntity<>(messages) {
        });
        return UriUtils.addPaginationLinks(response, page, maxPages, uriInfo).build();
    }

    @POST
    @PreAuthorize("@authService.isValidConversation(#createMessageDTO.driverId, #createMessageDTO.clientId)")
    @Consumes(value = {CustomMediaType.MESSAGE_CREATE})
    @Produces(value = {CustomMediaType.MESSAGE})
    public Response sendMessage(@Valid final CreateMessageDTO createMessageDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof PawUserDetails pawUserDetails)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        boolean loggedUserIsDriver = pawUserDetails.getUser().isDriver();
        Message message;
        if (loggedUserIsDriver) {
            Driver sender = driverService.findById(createMessageDTO.getDriverId());
            message = messageService.sendDriverMessageWithoutBooking(
                    sender,
                    createMessageDTO.getClientId(),
                    createMessageDTO.getMessage()
            );
        } else {
            Client sender = clientService.findById(createMessageDTO.getClientId());
            message = messageService.sendClientMessageWithoutBooking(
                    sender,
                    createMessageDTO.getDriverId(),
                    createMessageDTO.getMessage()
            );
        }
        return Response.created(
                uriInfo.getAbsolutePathBuilder().path(String.valueOf(message.getId())).build()
        ).entity(MessageDTO.fromMessage(uriInfo, message)).build();
    }


    @GET
    @Path("/{id}")
    @PreAuthorize("@authService.isAuthUserInConversation(#id)")
    @Produces(value = {CustomMediaType.MESSAGE})
    public Response getMessageById(@PathParam("id") final int id) {
        Message message = messageService.getMessageById(id);
        return Response.ok(MessageDTO.fromMessage(uriInfo, message)).build();
    }

}
