package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.webapp.validation.ValidMail;
import ar.edu.itba.paw.webapp.validation.ValidUsername;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public abstract class CreateUserDTO {
    @Size(min = 5, max = 20, message = "{Size.username}")
    @Pattern(regexp = "^[a-zA-Z]\\w*$", message = "{Pattern.username}")
    @ValidUsername
    private String username;

    @Email
    @ValidMail
    @NotBlank
    private String email;

    @Size(min = 8, max = 32, message = "{Size.password}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$", message = "{Pattern.password}")
    //TODO: change regex to a more simple one (as mentioned on tp2)
    private String password;

    public @Size(min = 5, max = 20) @Pattern(regexp = "^[a-zA-Z]\\w*$") String getUsername() {
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
