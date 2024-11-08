package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
public class Client extends User {

    public Client( String username, String mail, String password, Language language) {super(username, mail, password, language);}

    Client(){
        //This is used by hibernate. Do not remove.
    }


    @Override
    public boolean isClient() {
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Zone zone;

    public Zone getZone() {return zone;}

    public void setZone(Zone zone) {this.zone = zone;}
}
