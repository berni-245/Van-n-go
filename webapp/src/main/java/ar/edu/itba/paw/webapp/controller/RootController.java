package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.general.RootDTO;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(UriUtils.API_BASE_URI)
@Component
public class RootController {

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {
        RootDTO endpoints = new RootDTO();

        endpoints.setClientsUrl(getUriFor(ClientController.class));
        endpoints.setDriversUrl(getUriFor(DriverController.class));
        endpoints.setBookingsUrl(getUriFor(BookingController.class));
        endpoints.setZonesUrl(getUriFor(ZoneController.class));
        endpoints.setMessagesUrl(getUriFor(MessageController.class));

        endpoints.setDriverVehiclesUrl(getUriFor(VehicleController.class));

        endpoints.setClientUrl(getUriFor(ClientController.class) + "/{id}");
        endpoints.setDriverUrl(getUriFor(DriverController.class) + "/{id}");
        endpoints.setBookingUrl(getUriFor(BookingController.class) + "/{id}");
        endpoints.setZoneUrl(getUriFor(ZoneController.class) + "/{id}");
        endpoints.setVehicleUrl(getUriFor(VehicleController.class) + "/{vehicleId}");

        endpoints.setDriverSearchUrl(getUriFor(DriverController.class) + "{?zoneId,size,priceMax,weekday,rating,order,page}");
        endpoints.setVehicleSearchUrl(getUriFor(VehicleController.class) + "{?zoneId,size,priceMax,weekday,mustHaveAvailability,page}");

        return Response.ok(endpoints).build();
    }


    private String getUriFor(Class<?> controllerClass) {
        return uriInfo.getBaseUriBuilder().path(controllerClass).toTemplate();
    }
}