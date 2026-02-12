package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.general.ErrorDTO;
import ar.edu.itba.paw.webapp.dto.general.ErrorType;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper extends AbstractExceptionMapper<ConstraintViolationException> {

    @Override
    public Response generateResponse(ConstraintViolationException exception) {

        List<ErrorDTO> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {

            String fieldName = getLastPathNode(violation.getPropertyPath());

            // Can't use this exception message because we need a localized message
            errors.add(new ErrorDTO(ErrorType.INVALID_ARGUMENTS, violation.getMessage(), fieldName));
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<>(errors) {
                })
                .build();
    }

    private String getLastPathNode(Path path) {
        String lastNode = null;
        for (Path.Node node : path) {
            lastNode = node.getName();
        }
        return lastNode;
    }

}
