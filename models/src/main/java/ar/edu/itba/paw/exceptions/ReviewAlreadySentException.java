package ar.edu.itba.paw.exceptions;

public class ReviewAlreadySentException extends RuntimeException {
    public ReviewAlreadySentException() {
        super("The review was already sent to this booking");
    }
}
