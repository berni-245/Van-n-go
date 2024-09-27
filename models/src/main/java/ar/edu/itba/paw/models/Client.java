package ar.edu.itba.paw.models;

public class Client extends User {
    public Client(long id, String username, String mail, String password) {
        super(id, username, mail, password);
    }

    @Override
    public boolean isDriver() {
        return false;
    }
}
