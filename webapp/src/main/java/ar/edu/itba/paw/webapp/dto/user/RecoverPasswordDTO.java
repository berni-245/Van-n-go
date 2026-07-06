package ar.edu.itba.paw.webapp.dto.user;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class RecoverPasswordDTO {

    @Email
    private String mail;
    @Length(min = 6, max = 6)
    private String token;
    @Length(min = 8, max = 32)
    @Pattern(regexp = "^[A-Za-z\\d@$!%*#?&]+$", message = "{Pattern.password}")
    private String password;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
