package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static backend.constants.ErrorConst.ACCESS_DENIED;

/**
 * Exception class representing an access point restriction.
 * It is annotated with @ResponseStatus to indicate that an HTTP 403 status should be returned.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {


    /**
     * Default constructor for the AccessDeniedException.
     * It sets the error message to the constant ACCESS_DENIED and logs the error using SLF4J.
     */
    public AccessDeniedException() {
        super(ACCESS_DENIED);
        Logger logger = LoggerFactory.getLogger(AccessDeniedException.class);
        logger.error(ACCESS_DENIED, this);
    }
}

