package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.InvalidVehicleException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidVehicleExceptionMapper implements ExceptionMapper<InvalidVehicleException> {
    @Override
    public Response toResponse(InvalidVehicleException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDTO(e))
                .build();
    }
}
