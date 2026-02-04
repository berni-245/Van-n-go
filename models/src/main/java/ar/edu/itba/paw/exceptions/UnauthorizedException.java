package ar.edu.itba.paw.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Login in is needed using basic authentication");
    }
}
