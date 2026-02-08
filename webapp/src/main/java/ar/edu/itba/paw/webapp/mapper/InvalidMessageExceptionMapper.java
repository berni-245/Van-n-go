package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidMessageExceptionMapper implements ExceptionMapper<InvalidMessageException> {
    @Override
    public Response toResponse(InvalidMessageException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDTO(e))
                .build();
    }
}
