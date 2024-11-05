package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
public class Client extends User {

    public Client( String username, String mail, String password) {super(username, mail, password);}

    Client(){
        //This is used by hibernate. Do not remove.
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @ManyToOne
    private Zone zone;
    //TODO: Ver como solucionar el tema de la lazy intialization exception
    public Zone getZone() {return zone;}

    public void setZone(Zone zone) {this.zone = zone;}
}
