package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Driver extends User {

    @Column
    private String description;

    @Column(precision = 3)
    private Double rating;

    @Column(length = 32)
    private String cbu;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;

    public Driver(
            String username,
            String mail,
            String password,
            String description,
            Double rating,
            String cbu
    ) {
        super(username, mail, password);
        this.description = description;
        this.rating = rating;
        this.cbu = cbu;
    }

    Driver(){

    }

    public String getDescription() {
        return description;
    }

    public Double getRating() {
        return rating;
    }

    public String getCbu() {return cbu;}

    @Override
    public boolean isDriver() {
        return true;
    }

    public void setDescription(String description) { this.description = description; }

    public void setCbu(String cbu) { this.cbu = cbu; }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Driver{id=%d, username='%s', mail='%s', description='%s', cbu='%s'".formatted(
                getId(), getUsername(), getMail(), getDescription(), getCbu()
        );
    }
}
