package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.webapp.validation.ValidChangeMail;
import ar.edu.itba.paw.webapp.validation.ValidChangeUsername;
import ar.edu.itba.paw.webapp.validation.ValidZoneId;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@ValidChangeUsername
@ValidChangeMail
public class ChangeUserInfoForm {

    @NotNull
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z]\\w*$")
    private String oldUsername;
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String oldMail;

    @NotNull
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z]\\w*$")
    private String username;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String mail;

    @Nullable
    @ValidZoneId
    private Long zoneId;

    @Nullable
    @Length(min = 5, max = 255)
    private String description;

    //TODO:The previous Regex was wrong. No special chars on alias is fucking retarded
    @Length(max = 32)
    @Nullable
    private String cbu;

    public String getDescription() {return description;}

    public String getCbu() {return cbu;}

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

    public Long getZoneId() {return zoneId;}

    public void setZoneId(Long zoneId) {this.zoneId = zoneId;}

    public @NotNull @Size(min = 5, max = 20) @Pattern(regexp = "^[a-zA-Z]\\w*$") String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(@NotNull @Size(min = 5, max = 20) @Pattern(regexp = "^[a-zA-Z]\\w*$") String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public @NotNull @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") String getOldMail() {
        return oldMail;
    }

    public void setOldMail(@NotNull @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") String oldMail) {
        this.oldMail = oldMail;
    }
}
