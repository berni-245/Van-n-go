package ar.edu.itba.paw.webapp.auth;

public enum JwtTokenType {
    ACCESS("access"),
    REFRESH("refresh");

    private final String type;

    JwtTokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
