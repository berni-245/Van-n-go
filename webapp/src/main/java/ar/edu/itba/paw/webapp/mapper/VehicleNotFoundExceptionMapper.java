package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.VehicleNotFoundException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.ErrorType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleNotFoundExceptionMapper implements ExceptionMapper<VehicleNotFoundException> {

    @Override
    public Response toResponse(VehicleNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorDTO(ErrorType.VEHICLE_NOT_FOUND, e.getMessage()))
                .build();
    }
}
