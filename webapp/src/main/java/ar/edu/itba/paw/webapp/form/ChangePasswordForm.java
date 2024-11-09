package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.OldPasswordMatch;
import ar.edu.itba.paw.webapp.validation.PasswordMatch;
import ar.edu.itba.paw.webapp.validation.PasswordValidator;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatch
public class ChangePasswordForm implements PasswordValidator {
    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$")
    private String password;

    @Size(min = 8, max = 32)
    private String confirmPassword;

    @Size(min = 8, max = 32)
    @OldPasswordMatch
    private String oldPassword;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @Override
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
