package ar.edu.itba.paw.exceptions;

public class ForbiddenConversationException extends RuntimeException{
    public ForbiddenConversationException(){
        super("There was an attempt to access a forbidden conversation");
    }
}
