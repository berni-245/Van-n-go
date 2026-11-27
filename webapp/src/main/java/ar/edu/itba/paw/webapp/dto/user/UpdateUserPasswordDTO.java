package ar.edu.itba.paw.webapp.dto.user;

import ar.edu.itba.paw.webapp.validation.ValidPasswordChange;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@ValidPasswordChange
public class UpdateUserPasswordDTO {

    @Length(min = 8, max = 32)
    private String oldPassword;

    @Length(min = 8, max = 32)
    @Pattern(regexp = "^[A-Za-z\\d@$!%*#?&]+$", message = "{Pattern.password}")
    private String password;

    public UpdateUserPasswordDTO() {
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
