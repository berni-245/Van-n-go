package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.general.ErrorType;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        /*
         WebApplicationExceptions (404, 405, 406, redirects, etc.) already carry
         their own Response and are handled by Jersey's default behaviour. Since
         this generic mapper would otherwise take precedence for them, pass their
         original response through untouched instead of turning them into 500s.
         */
        if (exception instanceof WebApplicationException webException) {
            return webException.getResponse();
        }

        logger.error("Unhandled exception reached the generic mapper", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(ErrorType.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred."))
                .build();
    }
}
