package ar.edu.itba.paw.models;

public class Driver extends User {
    private final String extra1;

    private final Double rating;

    public Driver(
            long id,
            String username,
            String mail,
            String password,
            String extra1,
            Double rating
    ) {
        super(id, username, mail, password);
        this.extra1 = extra1;
        this.rating = rating;
    }

    public String getExtra1() {
        return extra1;
    }

    public Double getRating() {
        return rating;
    }

    @Override
    public boolean isDriver() {
        return true;
    }

    @Override
    public String toString() {
        return "Driver{id=%d, username='%s', mail='%s', extra1='%s'".formatted(
                getId(), getUsername(), getMail(), extra1
        );
    }
}
