package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidChangeMail;
import ar.edu.itba.paw.webapp.validation.ValidChangeUsername;
import ar.edu.itba.paw.webapp.validation.ValidLanguage;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class ChangeUserInfoForm {
    @NotNull
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z]\\w*$")
    @ValidChangeUsername
    private String username;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @ValidChangeMail
    private String mail;

    @Nullable
    @ValidZoneId
    private Integer zoneId;

    @Nullable
    @Length(min = 5, max = 255)
    private String description;

    //TODO:The previous Regex was wrong. No special chars on alias is wrong.
    @Length(max = 32)
    @Nullable
    private String cbu;

    @NotEmpty
    @ValidLanguage
    private String language;

    public String getDescription() {
        return description;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
