package ar.edu.itba.paw.exceptions;

public class MailAlreadyExistsException extends RuntimeException {
    public MailAlreadyExistsException() {
        super("Mail already exists");
    }
}
