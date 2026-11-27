package ar.edu.itba.paw.models;

import ar.edu.itba.paw.exceptions.PageOutOfBoundsException;

public class Pagination {
    public static final int BOOKINGS_PAGE_SIZE = 9;
    public static final int SEARCH_PAGE_SIZE = 12;
    public static final int VEHICLES_PAGE_SIZE = 6;
    public static final int MAX_MESSAGE_RETRIEVAL = 50;

    public static void validatePage(int page) {
        if (page < 1)
            throw new PageOutOfBoundsException();
    }
}
