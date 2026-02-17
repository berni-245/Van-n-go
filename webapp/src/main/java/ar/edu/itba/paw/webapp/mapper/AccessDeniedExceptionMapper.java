package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.general.ErrorType;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper extends AbstractExceptionMapper<AccessDeniedException> {

    @Override
    protected Response generateResponse(AccessDeniedException ex) {
        // Can't use this exception message because we need a localized message
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorDTO(
                        ErrorType.FORBIDDEN_ROLE_ACCESS,
                        "Your role isn't allowed to access this resource."
                ))
                .type(CustomMediaType.ERROR)
                .build();
    }
}



