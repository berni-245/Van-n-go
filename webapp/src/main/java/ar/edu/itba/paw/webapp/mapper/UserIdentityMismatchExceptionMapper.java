package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.UserIdentityMismatchException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.ErrorType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserIdentityMismatchExceptionMapper implements ExceptionMapper<UserIdentityMismatchException> {
    @Override
    public Response toResponse(UserIdentityMismatchException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorDTO(ErrorType.USER_IDENTITY_MISMATCH, e.getMessage()))
                .build();
    }
}
