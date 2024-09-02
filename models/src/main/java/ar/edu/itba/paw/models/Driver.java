package ar.edu.itba.paw.models;

public class Driver extends User {
    private final String extra1;

    public Driver(long id, String username, String mail, String extra1) {
        super(id, username, mail);
        this.extra1 = extra1;
    }

    public String getExtra1() {
        return extra1;
    }
}
