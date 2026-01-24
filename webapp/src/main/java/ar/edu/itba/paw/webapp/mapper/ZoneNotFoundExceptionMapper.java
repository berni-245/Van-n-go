package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ZoneNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ZoneNotFoundExceptionMapper implements ExceptionMapper<ZoneNotFoundException> {
    @Override
    public Response toResponse(ZoneNotFoundException e) {
        return Response.status(Response.Status.BAD_REQUEST).header("msg", "Invalid Zone Id").build();
    }
}