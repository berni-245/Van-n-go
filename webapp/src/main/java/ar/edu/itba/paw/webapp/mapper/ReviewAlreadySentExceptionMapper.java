package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ReviewAlreadySentException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ReviewAlreadySentExceptionMapper extends AbstractExceptionMapper<ReviewAlreadySentException> {
    @Override
    public Response generateResponse(ReviewAlreadySentException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}
