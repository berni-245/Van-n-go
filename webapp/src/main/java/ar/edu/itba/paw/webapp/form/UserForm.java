package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidMail;

import javax.validation.constraints.*;

public class UserForm {

    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z]\\w*$")
    private String username;

    @NotBlank
    @Email
    @ValidMail
    private String mail;

    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$")
    private String password;

    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$")
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match")
    public boolean passwordMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
