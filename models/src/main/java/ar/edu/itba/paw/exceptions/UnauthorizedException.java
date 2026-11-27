package ar.edu.itba.paw.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String msg) {
        super(msg);
    }

    public UnauthorizedException() {
        super("Login is needed using valid credentials for basic authentication or token authentication");
    }
}
