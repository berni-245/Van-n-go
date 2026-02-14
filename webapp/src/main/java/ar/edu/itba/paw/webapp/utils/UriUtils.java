package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UriUtils {
    public static final String API_BASE_URI = "/api";
    public static final String DRIVERS_URL = API_BASE_URI + "/drivers";
    public static final String CLIENTS_URL = API_BASE_URI + "/clients";
    public static final String ZONES_URL = API_BASE_URI + "/zones";
    public static final String BOOKINGS_URL = API_BASE_URI + "/bookings";
    public static final String MESSAGES_URL = API_BASE_URI + "/messages";

    public static Response.ResponseBuilder addPaginationLinks(Response.ResponseBuilder response, int requestedPage, int maxPages, UriInfo uriInfo) {
        response.link(buildPageUri(uriInfo, 1), "first");
        response.link(buildPageUri(uriInfo, maxPages), "last");

        if (requestedPage > 1)
            response.link(buildPageUri(uriInfo, requestedPage - 1), "prev");


        if (requestedPage < maxPages)
            response.link(buildPageUri(uriInfo, requestedPage + 1), "next");

        return response;
    }

    private static URI buildPageUri(UriInfo uriInfo, int page) {
        return uriInfo.getRequestUriBuilder()
                .replaceQueryParam("page", page)
                .build();
    }
}
