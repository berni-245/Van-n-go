package ar.edu.itba.paw.webapp.auth;

public enum JwtTokenType {
    AUTH("auth"),
    REFRESH("refresh");

    private final String type;

    JwtTokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
