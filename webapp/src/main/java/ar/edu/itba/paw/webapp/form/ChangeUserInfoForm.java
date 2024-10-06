package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidChangeMail;
import ar.edu.itba.paw.webapp.validation.ValidChangePasssword;
import ar.edu.itba.paw.webapp.validation.ValidChangeUsername;


@ValidChangeUsername
@ValidChangeMail
@ValidChangePasssword
public class ChangeUserInfoForm {


    private boolean mailChanged;

    private boolean usernameChanged;

    private boolean passwordChanged;

    private String username;

    private String mail;

    private String password;

    private String confirmPassword;

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

    public boolean getMailChanged() {
        return mailChanged;
    }

    public void setMailChanged(boolean mailChanged) {
        this.mailChanged = mailChanged;
    }

    public boolean getUsernameChanged() {
        return usernameChanged;
    }

    public void setUsernameChanged(boolean usernameChanged) {
        this.usernameChanged = usernameChanged;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

}
