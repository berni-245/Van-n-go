package ar.edu.itba.paw.exceptions;

public class PageOutOfBoundsException extends IndexOutOfBoundsException {
    public PageOutOfBoundsException() {
        super("Page must be an integer number starting from 1");
    }
}
