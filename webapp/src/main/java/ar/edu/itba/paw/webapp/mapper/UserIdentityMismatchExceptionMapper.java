package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.UserIdentityMismatchException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserIdentityMismatchExceptionMapper implements ExceptionMapper<UserIdentityMismatchException> {
    @Override
    public Response toResponse(UserIdentityMismatchException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorDTO(e))
                .build();
    }
}
