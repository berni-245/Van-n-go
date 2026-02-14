package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.PageOutOfBoundsException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class PageOutOfBoundsExceptionMapper extends AbstractExceptionMapper<PageOutOfBoundsException> {
    @Override
    protected Response generateResponse(PageOutOfBoundsException e) {
        return Response.status(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE)
                .entity(new ErrorDTO(e))
                .build();
    }
}
