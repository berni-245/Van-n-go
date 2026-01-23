package ar.edu.itba.paw.models;

import java.util.Arrays;

public enum SearchOrder {
    ALPHABETICAL,
    LEAST_PRICE,
    BEST_RATING,
    ;

    // TODO, hacer una interfaz para los enums que tengan este método
    public SearchOrder fromString(String order) {
        return valueOf(order.toUpperCase());
    }
}
