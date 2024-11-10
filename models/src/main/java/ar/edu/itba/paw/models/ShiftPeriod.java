package ar.edu.itba.paw.models;

public enum ShiftPeriod {
    MORNING("morning"),
    AFTERNOON("afternoon"),
    EVENING("evening");

    private final String lowerCaseText;

    ShiftPeriod(String lowerCaseText) {
        this.lowerCaseText = lowerCaseText;
    }

    public String getLowerCaseText() {
        return lowerCaseText;
    }
}
