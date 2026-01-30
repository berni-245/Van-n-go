package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Message;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;

public class MessageDTO {

    private String content;
    private boolean sentByDriver;
    private LocalDateTime timeSent;
    private URI self;
    private URI driver;
    private URI client;

    public static Function<Message, MessageDTO> mapper(UriInfo uriinfo) {
        return (message) -> getContentFromMessage(uriinfo, message);
    }

    public static MessageDTO getContentFromMessage(UriInfo uriInfo, Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setContent(message.getContent());
        dto.setSentByDriver(message.isSentByDriver());
        dto.setTimeSent(message.getTimeSent());
        dto.setSelf(uriInfo.getBaseUriBuilder()
                .path("api").path("messages")
                .path(String.valueOf(message.getId()))
                .build());
        return dto;
    }

    public static MessageDTO fromMessage(UriInfo uriInfo, Message message) {
        MessageDTO dto = getContentFromMessage(uriInfo, message);
        dto.setDriver(uriInfo.getBaseUriBuilder()
                .path("api").path("drivers")
                .path(String.valueOf(message.getDriver().getId()))
                .build());
        dto.setClient(uriInfo.getBaseUriBuilder()
                .path("api").path("clients")
                .path(String.valueOf(message.getClient().getId()))
                .build());
        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getDriver() {
        return driver;
    }

    public void setDriver(URI driver) {
        this.driver = driver;
    }

    public URI getClient() {
        return client;
    }

    public void setClient(URI client) {
        this.client = client;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSentByDriver() {
        return sentByDriver;
    }

    public void setSentByDriver(boolean sentByDriver) {
        this.sentByDriver = sentByDriver;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }
}
