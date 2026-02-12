package ar.edu.itba.paw.webapp.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;


public abstract class AbstractExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response toResponse(E exception) {
        logger.error("{}: {}", exception.getClass().getSimpleName(), exception.getMessage());
        return generateResponse(exception);
    }

    protected abstract Response generateResponse(E exception);
}