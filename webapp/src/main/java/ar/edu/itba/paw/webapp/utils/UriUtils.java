package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UriUtils {

    // Path segments
    public static final String API = "api";
    public static final String DRIVERS = "drivers";
    public static final String CLIENTS = "clients";
    public static final String ZONES = "zones";
    public static final String BOOKINGS = "bookings";
    public static final String MESSAGES = "messages";
    public static final String USERS = "users";
    public static final String VEHICLES = "vehicles";
    public static final String PROFILE_PICTURE = "profile-picture";
    public static final String PICTURE = "picture";
    public static final String AVAILABILITY = "availability";
    public static final String BOOKED_TIMES = "booked-times";
    public static final String PROOF_OF_PAYMENT = "proof-of-payment";
    public static final String REVIEW = "review";

    // Path/Query parameters
    public static final String ID_PARAM = "id";
    public static final String DRIVER_ID_PARAM = "driverId";
    public static final String VEHICLE_ID_PARAM = "vehicleId";
    public static final String BOOKING_ID_PARAM = "bookingId";
    public static final String CLIENT_ID_PARAM = "clientId";
    public static final String PAGE_PARAM = "page";
    public static final String USER_ID_PARAM = "userId";
    public static final String STATE_PARAM = "state";

    // Base URIs
    public static final String API_BASE_URI = "/" + API;
    public static final String DRIVERS_URL = API_BASE_URI + "/" + DRIVERS;
    public static final String CLIENTS_URL = API_BASE_URI + "/" + CLIENTS;
    public static final String ZONES_URL = API_BASE_URI + "/" + ZONES;
    public static final String BOOKINGS_URL = API_BASE_URI + "/" + BOOKINGS;
    public static final String MESSAGES_URL = API_BASE_URI + "/" + MESSAGES;
    public static final String USERS_URL = API_BASE_URI + "/" + USERS;
    public static final String VEHICLES_URL = DRIVERS_URL + "/{" + DRIVER_ID_PARAM + "}/" + VEHICLES;

    // URI builders
    public static UriBuilder apiBase(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(API);
    }

    public static UriBuilder driverUri(UriInfo uriInfo, int driverId) {
        return apiBase(uriInfo).path(DRIVERS).path(String.valueOf(driverId));
    }

    public static UriBuilder driverVehiclesUri(UriInfo uriInfo, int driverId) {
        return driverUri(uriInfo, driverId).path(VEHICLES);
    }

    public static UriBuilder vehicleUri(UriInfo uriInfo, int driverId, int vehicleId) {
        return driverVehiclesUri(uriInfo, driverId).path(String.valueOf(vehicleId));
    }

    public static UriBuilder clientUri(UriInfo uriInfo, int clientId) {
        return apiBase(uriInfo).path(CLIENTS).path(String.valueOf(clientId));
    }

    public static UriBuilder zoneUri(UriInfo uriInfo, int zoneId) {
        return apiBase(uriInfo).path(ZONES).path(String.valueOf(zoneId));
    }

    public static UriBuilder bookingUri(UriInfo uriInfo, int bookingId) {
        return apiBase(uriInfo).path(BOOKINGS).path(String.valueOf(bookingId));
    }

    public static UriBuilder bookingsForUser(UriInfo uriInfo, int userId) {
        return apiBase(uriInfo).path(BOOKINGS).queryParam(USER_ID_PARAM, userId);
    }

    public static UriBuilder messageUri(UriInfo uriInfo, int messageId) {
        return apiBase(uriInfo).path(MESSAGES).path(String.valueOf(messageId));
    }

    public static UriBuilder userUri(UriInfo uriInfo, String userType, int userId) {
        return apiBase(uriInfo).path(userType + "s").path(String.valueOf(userId));
    }

    // Pagination
    public static Response.ResponseBuilder addPaginationLinks(Response.ResponseBuilder response, int requestedPage, int maxPages, UriInfo uriInfo) {
        response.link(buildPageUri(uriInfo, 1), "first");
        response.link(buildPageUri(uriInfo, maxPages), "last");

        if (1 < requestedPage && requestedPage <= maxPages + 1)
            response.link(buildPageUri(uriInfo, requestedPage - 1), "prev");


        if (requestedPage < maxPages)
            response.link(buildPageUri(uriInfo, requestedPage + 1), "next");

        return response;
    }

    private static URI buildPageUri(UriInfo uriInfo, int page) {
        return uriInfo.getRequestUriBuilder()
                .replaceQueryParam(PAGE_PARAM, page)
                .build();
    }
}
