package ar.edu.itba.paw.webapp.validation;

public interface PasswordValidator {
    String getPassword();

    void setPassword(String password);

    String getConfirmPassword();

    void setConfirmPassword(String confirmPassword);
}
