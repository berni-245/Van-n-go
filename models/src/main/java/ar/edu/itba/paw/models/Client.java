package ar.edu.itba.paw.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class Client extends User {

    Client() {
        //This is used by hibernate. Do not remove.
    }

    public Client(String username, String mail, String password, Zone zone, Language language) {
        super(username, mail, password, language);
        this.zone = zone;
    }

    @Override
    public boolean isDriver() {
        return false;
    }


    @Override
    public boolean isClient() {
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Zone zone;

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    public String getType() {
        return "client";
    }
}
