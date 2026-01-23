package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public abstract class UserDTO {

    private String username;
    private String mail;
    private Language language;
    private URI self;
    private URI pfp;

    protected void setFromUser(UriInfo uriInfo, User user) {
        this.username = user.getUsername();
        this.mail = user.getMail();
        this.language = user.getLanguage();
        this.self = uriInfo.getBaseUriBuilder().path(user.getType()).path(String.valueOf(user.getId())).build();
        if (user.getPfp() != null) {
            this.pfp = uriInfo.getBaseUriBuilder().path("api").path("user").path("pfp").path(String.valueOf(user.getPfp())).build();
        } else {
            this.pfp = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getPfp() {
        return pfp;
    }

    public void setPfp(URI pfp) {
        this.pfp = pfp;
    }
}
