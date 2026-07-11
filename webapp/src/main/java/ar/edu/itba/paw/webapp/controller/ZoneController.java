package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.dto.general.ZoneDTO;
import ar.edu.itba.paw.webapp.utils.CacheControlUtils;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import ar.edu.itba.paw.webapp.utils.UriUtils;
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

@Path(UriUtils.ZONES_URL)
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
        return CacheControlUtils.setShortMaxTimeToResponse(Response.ok(new GenericEntity<>(zones) {
        }));
    }

    @GET
    @Path("/{id}")
    @Produces(value = {CustomMediaType.ZONE})
    public Response getZoneById(@PathParam(UriUtils.ID_PARAM) final int id) {
        Zone zone = zoneService.getZone(id).orElseThrow(ZoneNotFoundException::new);
        return CacheControlUtils.setLongMaxTimeToResponse(Response.ok(ZoneDTO.fromZone(uriInfo, zone)));
    }
}
