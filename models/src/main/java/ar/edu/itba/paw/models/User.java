package ar.edu.itba.paw.models;

public abstract class User {
    private final long id;
    private final String username;
    private final String mail;
    private final String password;

    public User(long id, String username, String mail, String password) {
        this.id = id;
        this.username = username;
        this.mail = mail;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public abstract boolean isDriver();

    public boolean getIsDriver() {
        return isDriver();
    }

    @Override
    public String toString() {
        return "User{id = %d, username='%s', mail='%s'}".formatted(id, username, mail);
    }
}
