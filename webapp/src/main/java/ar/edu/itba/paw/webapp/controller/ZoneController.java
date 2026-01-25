package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.dto.ZoneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/api/zones")
@Component
public class ZoneController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ZoneService zoneService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response listZones(){
        List<ZoneDTO> zones = zoneService.getAllZones().stream().map(ZoneDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(zones) {
        }).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getZoneById(@PathParam("id") final int id) {
        Zone zone = zoneService.getZone(id).orElseThrow(ZoneNotFoundException::new);
        return Response.ok(ZoneDTO.fromZone(uriInfo, zone)).build();
    }
}
