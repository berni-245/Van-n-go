package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.InvalidMessageException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidMessageExceptionMapper extends AbstractExceptionMapper<InvalidMessageException> {
    @Override
    public Response generateResponse(InvalidMessageException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDTO(e))
                .build();
    }
}
