package ar.edu.itba.paw.webapp.mapper;

import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper extends AbstractExceptionMapper<AccessDeniedException> {

    @Override
    protected Response generateResponse(AccessDeniedException ex) {
        // Can't use this exception message because we need a localized message
        return Response.status(Response.Status.FORBIDDEN).build();
    }
}



