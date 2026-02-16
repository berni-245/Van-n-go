package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.webapp.validation.ValidMail;
import ar.edu.itba.paw.webapp.validation.ValidUsername;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public abstract class CreateUserDTO {
    @Length(min = 5, max = 20, message = "{Size.username}")
    @Pattern(regexp = "^[a-zA-Z]\\w*$", message = "{Pattern.username}")
    @ValidUsername
    private String username;

    @Email
    @ValidMail
    @NotBlank
    private String email;

    @Length(min = 8, max = 32, message = "{Size.password}")
    @Pattern(regexp = "^[A-Za-z\\d@$!%*#?&]+$", message = "{Pattern.password}")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
