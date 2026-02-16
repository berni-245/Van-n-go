package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ForbiddenDriverCancelBookingException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenDriverCancelBookingExceptionMapper extends AbstractExceptionMapper<ForbiddenDriverCancelBookingException> {
    @Override
    public Response generateResponse(ForbiddenDriverCancelBookingException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
