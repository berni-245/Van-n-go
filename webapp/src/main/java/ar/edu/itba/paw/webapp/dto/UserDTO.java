package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.BookingState;
import ar.edu.itba.paw.models.Language;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public abstract class UserDTO {

    private int id;
    private String username;
    private String password;
    private String email;
    private Language preferredLanguage;
    private URI self;
    private URI proflePic;
    private URI pendingBookings;
    private URI acceptedBookings;
    private URI finishedBookings;
    private URI rejectedBookings;
    private URI canceledBookings;

    protected void setFromUser(UriInfo uriInfo, User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getMail();
        this.preferredLanguage = user.getLanguage();
        UriBuilder userIdEndpointTemplate = uriInfo.getBaseUriBuilder().path("api").path(user.getType() + "s").path(String.valueOf(user.getId()));
        this.self = userIdEndpointTemplate.clone().build();
        if (user.getPfp() != null) {
            this.proflePic = userIdEndpointTemplate.clone().path("profile-picture").build();
        } else {
            this.proflePic = null;
        }
        String userTypeQueryParam = user.getType().equals("driver") ? "toDriver" : "fromClient";
        UriBuilder bookingTemplate = uriInfo
                .getBaseUriBuilder().path("api").path("bookings")
                .queryParam(userTypeQueryParam, user.getId());
        this.pendingBookings = bookingTemplate.clone().queryParam("status", BookingState.PENDING.name()).build();
        this.acceptedBookings = bookingTemplate.clone().queryParam("status", BookingState.ACCEPTED.name()).build();
        this.finishedBookings = bookingTemplate.clone().queryParam("status", BookingState.FINISHED.name()).build();
        this.rejectedBookings = bookingTemplate.clone().queryParam("status", BookingState.REJECTED.name()).build();
        this.canceledBookings = bookingTemplate.clone().queryParam("status", BookingState.CANCELED.name()).build();
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getProflePic() {
        return proflePic;
    }

    public void setProflePic(URI proflePic) {
        this.proflePic = proflePic;
    }

    public URI getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(URI pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public URI getAcceptedBookings() {
        return acceptedBookings;
    }

    public void setAcceptedBookings(URI acceptedBookings) {
        this.acceptedBookings = acceptedBookings;
    }

    public URI getFinishedBookings() {
        return finishedBookings;
    }

    public void setFinishedBookings(URI finishedBookings) {
        this.finishedBookings = finishedBookings;
    }

    public URI getRejectedBookings() {
        return rejectedBookings;
    }

    public void setRejectedBookings(URI rejectedBookings) {
        this.rejectedBookings = rejectedBookings;
    }

    public URI getCanceledBookings() {
        return canceledBookings;
    }

    public void setCanceledBookings(URI canceledBookings) {
        this.canceledBookings = canceledBookings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
