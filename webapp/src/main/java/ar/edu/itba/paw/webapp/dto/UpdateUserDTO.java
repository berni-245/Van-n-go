package ar.edu.itba.paw.webapp.dto;

public class UpdateUserDTO {
    private String username;
    private String email;
    private String preferredLanguage;

    public UpdateUserDTO() {

    }

    public String getUsername() {
        return username;
    }

    public String getUsernameOr(String other) {
        if (username == null)
            return other;
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailOr(String other) {
        if (email == null)
            return other;
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public String getPreferredLanguageOr(String other) {
        if (preferredLanguage == null)
            return other;
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
