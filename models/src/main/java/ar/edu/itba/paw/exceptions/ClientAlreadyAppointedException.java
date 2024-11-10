package ar.edu.itba.paw.exceptions;

public class ClientAlreadyAppointedException extends RuntimeException{
    public ClientAlreadyAppointedException() {
        super("The client already appointed for that vehicle at that zone and time");
    }
}