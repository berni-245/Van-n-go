package ar.edu.itba.paw.exceptions;

public class PageOutOfBoundsException extends IndexOutOfBoundsException {
    public PageOutOfBoundsException(int maxPages) {
        super("Page must be between 1 and %d".formatted(maxPages));
    }
}
