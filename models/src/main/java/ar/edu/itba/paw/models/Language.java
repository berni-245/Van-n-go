package ar.edu.itba.paw.models;

import java.util.Locale;

public enum Language {
    ENGLISH("en"), SPANISH("es");

    final String toLocale;

    Language(String locale) {
        this.toLocale = locale;
    }

    public String toLocale() {
        return toLocale;
    }

    public static Language fromLocale(Locale locale) {
        if (locale.getLanguage().equals(ENGLISH.toLocale())) {
            return ENGLISH;
        } else if (locale.getLanguage().equals(SPANISH.toLocale())) {
            return SPANISH;
        }
        return null;
    }
}
