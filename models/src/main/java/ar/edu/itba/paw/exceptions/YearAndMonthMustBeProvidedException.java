package ar.edu.itba.paw.exceptions;

public class YearAndMonthMustBeProvidedException extends RuntimeException {
    public YearAndMonthMustBeProvidedException() {
        super("Year and Month cannot be null");
    }
}
