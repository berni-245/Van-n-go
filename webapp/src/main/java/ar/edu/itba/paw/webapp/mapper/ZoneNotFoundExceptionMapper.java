package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.webapp.mapper.util.ErrorResponseBuilder;
import ar.edu.itba.paw.webapp.mapper.util.ErrorType;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ZoneNotFoundExceptionMapper
        implements ExceptionMapper<ZoneNotFoundException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ZoneNotFoundException e) {
        return ErrorResponseBuilder.builder()
                .type(ErrorType.ZONE_NOT_FOUND)
                .status(Response.Status.NOT_FOUND)
                .title(e.getMessage())
                .detail(e.getMessage()) // TODO use a better detail or don't add it (it's not mandatory in the RTC 9457)
                .instance(uriInfo.getRequestUri())
                .build();
    }
}
