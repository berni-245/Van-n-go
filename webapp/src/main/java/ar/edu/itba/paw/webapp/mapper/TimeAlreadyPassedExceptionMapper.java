package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.TimeAlreadyPassedException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class TimeAlreadyPassedExceptionMapper extends AbstractExceptionMapper<TimeAlreadyPassedException> {
    @Override
    public Response generateResponse(TimeAlreadyPassedException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}
