package ar.edu.itba.paw.models;

public class Pagination {
    public static final int BOOKINGS_PAGE_SIZE = 9;
    public static final int SEARCH_PAGE_SIZE = 12;
    public static final int VEHICLES_PAGE_SIZE = 6;
    public static final int MAX_MESSAGE_RETRIEVAL = 100;

    public static int validatePage(int page, int totalPages) {
        if (totalPages == 0) return 1;
        return Math.clamp(page, 1, totalPages);
    }
}
