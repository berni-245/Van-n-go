package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.utils.CustomMediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ImageNotFoundExceptionMapper extends AbstractExceptionMapper<ImageNotFoundException> {
    @Override
    protected Response generateResponse(ImageNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(CustomMediaType.ERROR)
                .entity(new ErrorDTO(e))
                .build();
    }
}
