package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.VehicleHasAcceptedJobsException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class VehicleHasPendingJobsExceptionMapper implements ExceptionMapper<VehicleHasAcceptedJobsException> {
    @Override
    public Response toResponse(VehicleHasAcceptedJobsException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorDTO(e))
                .build();
    }
}
