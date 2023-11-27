package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static backend.constants.ErrorConst.VALIDATION_FAILED;

/**
 * Exception class representing a data validation failure.
 * It is annotated with @ResponseStatus to indicate that an HTTP 406 status should be returned.
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class DataValidationException extends RuntimeException {

    /**
     * Default constructor for the DataValidationException.
     * It sets the error message to the constant VALIDATION_FAILED and logs the error using SLF4J.
     */
    public DataValidationException() {
        super(VALIDATION_FAILED);
        Logger logger = LoggerFactory.getLogger(DataValidationException.class);
        logger.error(VALIDATION_FAILED, this);
    }
}