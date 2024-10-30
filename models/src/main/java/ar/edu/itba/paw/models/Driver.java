package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Driver extends User {

    @Column
    private  String extra1;

    @Column(precision = 3)
    private  Double rating;

    @Column(length = 32)
    private  String cbu;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;

    public Driver(
            String username,
            String mail,
            String password,
            String extra1,
            Double rating,
            String cbu
    ) {
        super(username, mail, password);
        this.extra1 = extra1;
        this.rating = rating;
        this.cbu = cbu;
    }



    Driver(){

    }

    public String getExtra1() {
        return extra1;
    }

    public Double getRating() {
        return rating;
    }

    public String getCbu() {return cbu;}

    @Override
    public boolean isDriver() {
        return true;
    }

    public void setExtra1(String extra1) { this.extra1 = extra1; }

    public void setCbu(String cbu) { this.cbu = cbu; }



    @Override
    public String toString() {
        return "Driver{id=%d, username='%s', mail='%s', extra1='%s', cbu='%s'".formatted(
                getId(), getUsername(), getMail(), extra1, cbu
        );
    }
}
