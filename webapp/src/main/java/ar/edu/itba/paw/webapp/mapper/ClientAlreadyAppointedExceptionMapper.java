package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ClientAlreadyAppointedException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientAlreadyAppointedExceptionMapper implements ExceptionMapper<ClientAlreadyAppointedException> {
    @Override
    public Response toResponse(ClientAlreadyAppointedException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}
