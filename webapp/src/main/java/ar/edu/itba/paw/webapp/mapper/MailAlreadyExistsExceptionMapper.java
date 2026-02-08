package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.MailAlreadyExistsException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MailAlreadyExistsExceptionMapper implements ExceptionMapper<MailAlreadyExistsException> {

    @Override
    public Response toResponse(MailAlreadyExistsException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}