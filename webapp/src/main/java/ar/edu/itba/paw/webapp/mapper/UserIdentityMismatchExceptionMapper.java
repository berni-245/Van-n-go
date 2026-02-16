package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.UserIdentityMismatchException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class UserIdentityMismatchExceptionMapper extends AbstractExceptionMapper<UserIdentityMismatchException> {
    @Override
    public Response generateResponse(UserIdentityMismatchException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
