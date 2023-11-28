package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static backend.constants.ErrorConst.MISSING_PARAMETER;

/**
 * Exception class representing a missing parameter from the controller.
 * It is annotated with @ResponseStatus to indicate that an HTTP 400 status should be returned.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingParameterException extends RuntimeException {

    /**
     * Default constructor for the MissingParameterException.
     * It sets the error message to the constant MISSING_PARAMETER and logs the error using SLF4J.
     */
    public MissingParameterException() {
        super(MISSING_PARAMETER);
        Logger logger = LoggerFactory.getLogger(MissingParameterException.class);
        logger.error(MISSING_PARAMETER, this);
    }
}