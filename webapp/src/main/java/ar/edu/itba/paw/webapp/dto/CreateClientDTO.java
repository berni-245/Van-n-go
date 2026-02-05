package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validation.ValidMail;
import ar.edu.itba.paw.webapp.validation.ValidUsername;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateClientDTO {

    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z]\\w*$")
    @ValidUsername
    private String username;

    @Email
    @ValidMail
    @NotBlank
    private String email;

    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$")
    //TODO: change regex to a more simple one (as mentioned on tp2)
    private String password;

    @ValidZoneId
    private Integer zoneId;

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

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

}
