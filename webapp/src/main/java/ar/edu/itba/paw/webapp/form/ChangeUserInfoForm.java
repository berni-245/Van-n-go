package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidChangeMail;
import ar.edu.itba.paw.webapp.validation.ValidChangeUsername;


@ValidChangeUsername
@ValidChangeMail
public class ChangeUserInfoForm {


    private boolean mailChanged;


    private boolean usernameChanged;



    private String username;



    private String mail;

   /* @Nullable
    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$")
    private String password;

    @Nullable
    @Size(min = 8, max = 32)
    private String confirmPassword;
*/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

 /*   public String getPassword() {
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
    }*/

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

    public String getMail(){
        return mail;
    }

    public void setMail(String mail){
        this.mail = mail;
    }
}
