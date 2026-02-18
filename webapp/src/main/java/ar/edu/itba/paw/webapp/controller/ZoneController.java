package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.dto.general.ZoneDTO;
import ar.edu.itba.paw.webapp.utils.CacheControlUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/zones")
@Component
public class ZoneController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ZoneService zoneService;

    @GET
    @Produces(value = {CustomMediaType.ZONE_LIST})
    public Response listZones(){
        List<ZoneDTO> zones = zoneService.getAllZones().stream().map(ZoneDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(zones) {
        }).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {CustomMediaType.ZONE})
    public Response getZoneById(@PathParam("id") final int id) {
        Zone zone = zoneService.getZone(id).orElseThrow(ZoneNotFoundException::new);
        return CacheControlUtils.setMaxTimeToResponse(Response.ok(ZoneDTO.fromZone(uriInfo, zone)));
    }
}
