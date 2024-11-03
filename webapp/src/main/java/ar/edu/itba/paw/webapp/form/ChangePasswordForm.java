package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.OldPasswordMatch;
import ar.edu.itba.paw.webapp.validation.PasswordMatch;
import ar.edu.itba.paw.webapp.validation.PasswordValidator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatch
@OldPasswordMatch
public class ChangePasswordForm implements PasswordValidator {

    @NotNull
    private long userId;

    @NotNull
    private boolean isDriver;

    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$")
    private String password;

    @Size(min = 8, max = 32)
    private String confirmPassword;

    @Size(min = 8, max = 32)
    private String oldPassword;

    @NotNull
    public long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull long userId) {
        this.userId = userId;
    }

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

    public @Size(min = 8, max = 32) String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(@Size(min = 8, max = 32) String oldPassword) {
        this.oldPassword = oldPassword;
    }


    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }
}
