package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ForbiddenClientCancelBookingException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenClientCancelBookingExceptionMapper extends AbstractExceptionMapper<ForbiddenClientCancelBookingException> {
    @Override
    public Response generateResponse(ForbiddenClientCancelBookingException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
