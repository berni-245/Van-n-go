package ar.edu.itba.paw.models;

public class Driver extends User {
    private final String extra1;

    private final Double rating;

    private final String cbu;

    public Driver(
            long id,
            String username,
            String mail,
            String password,
            int pfp,
            String extra1,
            Double rating,
            String cbu
    ) {
        super(id, username, mail, password,pfp);
        this.extra1 = extra1;
        this.rating = rating;
        this.cbu = cbu;
    }

    public String getExtra1() {
        return extra1;
    }

    public Double getRating() {
        return rating;
    }

    public String getcbu() {return cbu;}

    @Override
    public boolean isDriver() {
        return true;
    }

    @Override
    public String toString() {
        return "Driver{id=%d, username='%s', mail='%s', extra1='%s', cbu='%s'".formatted(
                getId(), getUsername(), getMail(), extra1, cbu
        );
    }
}
