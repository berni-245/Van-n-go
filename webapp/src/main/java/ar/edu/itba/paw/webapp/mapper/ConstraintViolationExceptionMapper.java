package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.ErrorType;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        List<ErrorDTO> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            // violation.getPropertyPath() te da el nombre del campo (ej: "username")
            // violation.getMessage() te da el mensaje de error
            errors.add(new ErrorDTO(ErrorType.USER_ALREADY_EXISTS, violation.getMessage()));
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<>(errors) {
                })
                .build();
    }

}
