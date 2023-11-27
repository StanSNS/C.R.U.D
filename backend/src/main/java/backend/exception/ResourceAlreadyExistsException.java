package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static backend.constants.ErrorConst.RESOURCE_ALREADY_EXISTS;

/**
 * Exception class representing a conflict error when a resource already exists.
 * It is annotated with @ResponseStatus to indicate that an HTTP 409 status should be returned.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Default constructor for the ResourceAlreadyExistsException.
     * It sets the error message to the constant RESOURCE_ALREADY_EXISTS and logs the error using SLF4J.
     */
    public ResourceAlreadyExistsException() {
        super(RESOURCE_ALREADY_EXISTS);
        Logger logger = LoggerFactory.getLogger(ResourceAlreadyExistsException.class);
        logger.error(RESOURCE_ALREADY_EXISTS, this);
    }
}