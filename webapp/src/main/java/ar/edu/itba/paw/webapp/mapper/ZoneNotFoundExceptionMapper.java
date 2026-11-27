package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ZoneNotFoundException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ZoneNotFoundExceptionMapper extends AbstractExceptionMapper<ZoneNotFoundException> {

    @Override
    public Response generateResponse(ZoneNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
