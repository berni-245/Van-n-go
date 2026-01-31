package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Driver;
import ar.edu.itba.paw.models.Message;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.MessageService;
import ar.edu.itba.paw.webapp.dto.CreateMessageDTO;
import ar.edu.itba.paw.webapp.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response listZones(@QueryParam("driverId") Integer driverId,
                              @QueryParam("clientId") Integer clientId) {
        Client client = clientService.findById(clientId);
        Driver driver = driverService.findById(driverId);
        List<MessageDTO> messages = messageService.getConversationWithoutBooking(client, driver).stream()
                .map(MessageDTO.mapper(uriInfo))
                .toList();
        return Response.ok(new GenericEntity<>(messages) {
        }).build();
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response sendMessage(final CreateMessageDTO createMessageDTO) {
        boolean loggedUserIsDriver = true; //TODO: ver como podriamos obtener el User usando JWT (este enpoint si o si requiere estar autenticado)
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
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(message.getId())).build()).entity(MessageDTO.fromMessage(uriInfo, message)).build();
    }


    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getMessageById(@PathParam("id") final int id) {
        //TODO: Check permissions
        Message message = messageService.getMessageById(id);
        return Response.ok(MessageDTO.fromMessage(uriInfo, message)).build();
    }

}
