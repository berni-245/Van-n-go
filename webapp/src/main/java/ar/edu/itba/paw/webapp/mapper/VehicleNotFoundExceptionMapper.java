package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.VehicleNotFoundException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleNotFoundExceptionMapper extends AbstractExceptionMapper<VehicleNotFoundException> {

    @Override
    public Response generateResponse(VehicleNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorDTO(e))
                .build();
    }
}
