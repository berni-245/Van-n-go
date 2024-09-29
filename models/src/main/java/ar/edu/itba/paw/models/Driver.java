package ar.edu.itba.paw.models;

import java.util.List;

public class Driver extends User {
    private final String extra1;

    private List<Vehicle> vehicles;

    private final Double rating;

    public Driver(
            long id,
            String username,
            String mail,
            String password,
            String extra1,
            List<Vehicle> vehicles,
            Double rating
    ) {
        super(id, username, mail, password);
        this.extra1 = extra1;
        this.vehicles = vehicles;
        this.rating = rating;
    }

    public Driver(
            long id,
            String username,
            String mail,
            String password,
            String extra1,
            Double rating
    ) {
        this(id, username, mail, password, extra1, null, rating);
    }

    public Driver(User user, String extra1, Double rating) {
        this(user.getId(), user.getUsername(), user.getMail(), user.getPassword(), extra1, rating);
    }

    public String getExtra1() {
        return extra1;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public Double getRating() { return rating; }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public boolean isDriver() {
        return true;
    }

    @Override
    public String toString() {
        return "Driver{id=%d, username='%s', mail='%s', extra1='%s', vehicles='%s'".formatted(
                getId(), getUsername(), getMail(), extra1, vehicles
        );
    }
}
