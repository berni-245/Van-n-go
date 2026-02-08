package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ForbiddenBookingStateOperationException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenBookingStateOperationExceptionMapper implements ExceptionMapper<ForbiddenBookingStateOperationException> {
    @Override
    public Response toResponse(ForbiddenBookingStateOperationException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorDTO(e))
                .build();
    }
}
