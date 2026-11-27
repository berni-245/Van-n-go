package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.auth.ForbiddenClientInfoAccessException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenClientInfoAccessExceptionMapper extends AbstractExceptionMapper<ForbiddenClientInfoAccessException> {
    @Override
    protected Response generateResponse(ForbiddenClientInfoAccessException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
