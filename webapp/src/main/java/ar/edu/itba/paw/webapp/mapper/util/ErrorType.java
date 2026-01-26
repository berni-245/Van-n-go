package ar.edu.itba.paw.webapp.mapper.util;

import java.net.URI;

public enum ErrorType {
    ZONE_NOT_FOUND("zone-not-found"),
    USER_NOT_FOUND("user-not-found"),
    ;

    private final static String baseStringUri = "http://pawserver.it.itba.edu.ar/paw-2024b-01/errors/";
    private final String relativeStringUri;

    ErrorType(String relativeStringUri) {
        this.relativeStringUri = relativeStringUri;
    }

    public URI getURI() {
        return URI.create(baseStringUri + relativeStringUri);
    }
}