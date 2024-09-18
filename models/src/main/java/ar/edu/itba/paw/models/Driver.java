package ar.edu.itba.paw.models;

import java.util.List;

public class Driver extends User {
    private final String extra1;

    private List<Vehicle> vehicles;

    public Driver(
            long id,
            String username,
            String mail,
            String password,
            String extra1,
            List<Vehicle> vehicles
    ) {
        super(id, username, mail, password);
        this.extra1 = extra1;
        this.vehicles = vehicles;
    }

    public Driver(
            long id,
            String username,
            String mail,
            String password,
            String extra1
    ) {
        this(id, username, mail, password, extra1, null);
    }

    public Driver(User user, String extra1) {
        this(user.getId(), user.getUsername(), user.getMail(), user.getPassword(), extra1);
    }

    public String getExtra1() {
        return extra1;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
