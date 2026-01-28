package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.UserAlreadyExistsException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.ErrorType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserAlreadyExistsExceptionMapper implements ExceptionMapper<UserAlreadyExistsException> {

    @Override
    public Response toResponse(UserAlreadyExistsException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(ErrorType.USER_ALREADY_EXISTS, e.getMessage()))
                .build();
    }
}