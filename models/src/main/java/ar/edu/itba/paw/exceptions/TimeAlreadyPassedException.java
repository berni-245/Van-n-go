package ar.edu.itba.paw.exceptions;

public class TimeAlreadyPassedException extends RuntimeException{
    public TimeAlreadyPassedException(){
        super("Time already passed\n");
    }
}