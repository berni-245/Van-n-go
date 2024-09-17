package ar.edu.itba.paw.models;

public class Client extends User{
    public Client(long id, String username, String mail, String password) {
        super(id, username, mail, password);
    }

    public Client(User user) {
        this(user.getId(), user.getUsername(), user.getMail(), user.getPassword());
    }
}
