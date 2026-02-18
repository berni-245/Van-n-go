package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.YearAndMonthMustBeProvidedException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class YearAndMonthMustBeProvidedExceptionMapper extends AbstractExceptionMapper<YearAndMonthMustBeProvidedException> {
    @Override
    protected Response generateResponse(YearAndMonthMustBeProvidedException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
