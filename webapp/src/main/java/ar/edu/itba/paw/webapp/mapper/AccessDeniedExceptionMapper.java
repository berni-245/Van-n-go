package ar.edu.itba.paw.webapp.mapper;

import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper
        implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException ex) {
        return Response.status(Response.Status.FORBIDDEN)
                // Can't use this exception message because we need a localized message
                .build();
    }
}

