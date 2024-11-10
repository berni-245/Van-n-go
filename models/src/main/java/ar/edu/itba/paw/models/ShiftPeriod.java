package ar.edu.itba.paw.models;

public enum ShiftPeriod {
    MORNING("morning","Morning"),
    AFTERNOON("afternoon", "Afternoon"),
    EVENING("evening", "Evening");

    private final String lowerCaseText;

    private final String capitalizedText;

    ShiftPeriod(String lowerCaseText, String capitalizedText) {
        this.lowerCaseText = lowerCaseText;
        this.capitalizedText = capitalizedText;
    }

    public String getLowerCaseText() {
        return lowerCaseText;
    }

    public String getCapitalizedText() {return capitalizedText;}
}
