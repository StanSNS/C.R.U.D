package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static backend.constants.ErrorConst.RESOURCE_NOT_FOUND;

/**
 * Exception class representing a resource not found error.
 * It is annotated with @ResponseStatus to indicate that an HTTP 404 status should be returned.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Default constructor for the ResourceNotFoundException.
     * It sets the error message to the constant RESOURCE_NOT_FOUND and logs the error using SLF4J.
     */
    public ResourceNotFoundException() {
        super(RESOURCE_NOT_FOUND);
        Logger logger = LoggerFactory.getLogger(ResourceNotFoundException.class);
        logger.error(RESOURCE_NOT_FOUND, this);
    }
}