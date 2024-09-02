package ar.edu.itba.paw.models;

public class User {
    private final long id;
    private final String username;
    private final String mail;
//    private final String password;

    public User(long id, String username, String mail) {
        this.id = id;
        this.mail = mail;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }
}
