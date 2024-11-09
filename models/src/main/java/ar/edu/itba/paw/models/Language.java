package ar.edu.itba.paw.models;

import java.util.Locale;

public enum Language {
    ENGLISH(Locale.of("en")), SPANISH(Locale.of("es"));

    final Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
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
