package ar.edu.itba.paw.webapp.dto.user;

import javax.validation.constraints.Email;

public class MailDTO {

    @Email
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
