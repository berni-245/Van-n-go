package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.ErrorType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {
    @Override
    public Response toResponse(UnauthorizedException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorDTO(ErrorType.UNAUTHORIZED, e.getMessage()))
                .build();
    }
}
