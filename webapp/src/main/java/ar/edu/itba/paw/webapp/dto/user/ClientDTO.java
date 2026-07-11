package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;


public class ClientDTO extends UserDTO {

    private URI zone;

    public static ClientDTO fromPublicClientInfo(UriInfo uriInfo, Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setPublicInfoFromUser(uriInfo, client);
        return clientDTO;
    }

    public static ClientDTO fromClient(UriInfo uriInfo, Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setAllInfoFromUser(uriInfo, client);
        clientDTO.zone = UriUtils.zoneUri(uriInfo, client.getZone().getId()).build();
        return clientDTO;
    }

    public URI getZone() {
        return zone;
    }

    public void setZone(URI zone) {
        this.zone = zone;
    }
}
