package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Client;

import javax.ws.rs.core.UriInfo;


public class ClientDTO extends UserDTO {

    public static ClientDTO fromClient(UriInfo uriInfo, Client user) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setFromUser(uriInfo, user);
        //add more fields specific to ClientDTO here
        return clientDTO;
    }
}
