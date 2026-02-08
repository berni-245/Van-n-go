package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.VehicleNotAvailableException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleNotAvailableExceptionMapper implements ExceptionMapper<VehicleNotAvailableException> {
    @Override
    public Response toResponse(VehicleNotAvailableException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}
