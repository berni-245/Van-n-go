package ar.edu.itba.paw.models;

import java.util.Locale;

public enum Language {
    ENGLISH(Locale.of("en"),"english"), SPANISH(Locale.of("es"),"spanish");

    final Locale locale;

    private final String lowerCaseText;


    Language(Locale locale, String lowerCaseText) {
        this.locale = locale;
        this.lowerCaseText = lowerCaseText;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLowerCaseText() {
        return lowerCaseText;
    }

    public static Language fromLocale(Locale locale) {
        if (locale.getLanguage().equals(ENGLISH.locale.getLanguage())) {
            return ENGLISH;
        } else if (locale.getLanguage().equals(SPANISH.locale.getLanguage())) {
            return SPANISH;
        }
        return null;
    }
}
