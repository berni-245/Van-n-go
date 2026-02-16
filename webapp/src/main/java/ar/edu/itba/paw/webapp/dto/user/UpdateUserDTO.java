package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.webapp.validation.ValidChangeLanguage;
import ar.edu.itba.paw.webapp.validation.ValidChangeMail;
import ar.edu.itba.paw.webapp.validation.ValidChangeUsername;
import ar.edu.itba.paw.webapp.validation.ValidPasswordChange;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ValidPasswordChange
public abstract class UpdateUserDTO {

    @Size(min = 5, max = 20, message = "{Size.username}")
    @Pattern(regexp = "^[a-zA-Z]\\w*$", message = "{Pattern.username}")
    @ValidChangeUsername
    private String username;

    @Email
    @ValidChangeMail
    private String email;


    @ValidChangeLanguage
    private String preferredLanguage;

    @Size(min = 8, max = 32)
    private String oldPassword;

    @Size(min = 8, max = 32)
    @Pattern(regexp = "^[A-Za-z\\d@$!%*#?&]+$", message = "{Pattern.password}")
    private String password;

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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
