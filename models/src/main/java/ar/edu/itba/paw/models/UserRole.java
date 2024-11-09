package ar.edu.itba.paw.models;

public enum UserRole {
    CLIENT,
    DRIVER;

    public String role() {
        return "ROLE_" + name();
    }
}
