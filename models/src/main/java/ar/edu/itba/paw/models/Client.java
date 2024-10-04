package ar.edu.itba.paw.models;

public class Client extends User {
    public Client(long id, String username, String mail, String password, int pfp) {super(id, username, mail, password,pfp);}

    @Override
    public boolean isDriver() {
        return false;
    }
}
