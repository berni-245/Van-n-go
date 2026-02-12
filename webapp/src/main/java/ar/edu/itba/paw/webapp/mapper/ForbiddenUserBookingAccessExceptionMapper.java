package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ForbiddenUserBookingAccessException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenUserBookingAccessExceptionMapper extends AbstractExceptionMapper<ForbiddenUserBookingAccessException> {
    @Override
    public Response generateResponse(ForbiddenUserBookingAccessException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorDTO(e))
                .build();
    }
}
