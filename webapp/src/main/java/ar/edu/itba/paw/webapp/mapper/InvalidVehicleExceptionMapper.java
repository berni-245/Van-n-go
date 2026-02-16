package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.InvalidVehicleException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidVehicleExceptionMapper extends AbstractExceptionMapper<InvalidVehicleException> {
    @Override
    public Response generateResponse(InvalidVehicleException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
