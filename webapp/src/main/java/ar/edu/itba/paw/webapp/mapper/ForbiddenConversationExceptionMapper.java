package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ForbiddenConversationException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.general.ErrorType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenConversationExceptionMapper implements ExceptionMapper<ForbiddenConversationException> {
    @Override
    public Response toResponse(ForbiddenConversationException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorDTO(ErrorType.FORBIDDEN_CONVERSATION, e.getMessage()))
                .build();
    }
}
