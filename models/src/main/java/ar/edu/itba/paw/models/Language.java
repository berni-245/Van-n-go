package ar.edu.itba.paw.models;

import java.util.Locale;

public enum Language {
    ENGLISH("en"),SPANISH("es");

    final String toLocale;

    Language(String locale) {
        this.toLocale = locale;
    }

}
