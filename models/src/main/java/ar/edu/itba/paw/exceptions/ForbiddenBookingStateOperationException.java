package ar.edu.itba.paw.exceptions;

public class ForbiddenBookingStateOperationException extends RuntimeException{
    public ForbiddenBookingStateOperationException(){
        super("The booking is not allow to change to that state");
    }
}
