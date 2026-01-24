package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Client;

import javax.ws.rs.core.UriInfo;
import java.net.URI;


public class ClientDTO extends UserDTO {

    private URI zone;

    public static ClientDTO fromClient(UriInfo uriInfo, Client user) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setFromUser(uriInfo, user);
        if (user.getZone() != null) {
            clientDTO.zone = uriInfo.getBaseUriBuilder().path("api").path("zones").path(String.valueOf(user.getZone().getId())).build();
        }else{
            clientDTO.zone = null;
        }
        return clientDTO;
    }

    public URI getZone() {
        return zone;
    }

    public void setZone(URI zone) {
        this.zone = zone;
    }
}
