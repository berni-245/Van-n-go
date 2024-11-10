package ar.edu.itba.paw.models;

public enum ShiftPeriod {
    MORNING("generic.word.morning"),
    AFTERNOON("generic.word.afternoon"),
    EVENING("generic.word.evening");

    private final String code;

    ShiftPeriod(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
