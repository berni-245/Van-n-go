package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.VehicleIsAlreadyAcceptedException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleIsAlreadyAcceptedExceptionMapper extends AbstractExceptionMapper<VehicleIsAlreadyAcceptedException> {
    @Override
    public Response generateResponse(VehicleIsAlreadyAcceptedException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}
