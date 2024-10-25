package ar.edu.itba.paw.models;

import javax.persistence.Entity;

@Entity
public class Client extends User {

    public Client( String username, String mail, String password) {super(username, mail, password);}

    Client(){
        //This is used by hibernate. Do not remove.
    }

    @Override
    public boolean isDriver() {
        return false;
    }
}
