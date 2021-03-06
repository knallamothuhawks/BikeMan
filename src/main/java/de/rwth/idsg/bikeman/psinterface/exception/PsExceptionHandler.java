package de.rwth.idsg.bikeman.psinterface.exception;

import de.rwth.idsg.bikeman.web.rest.exception.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.PersistenceException;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.02.2015
 */
@ControllerAdvice(basePackages = "de.rwth.idsg.bikeman.psinterface.rest")
@Slf4j
public class PsExceptionHandler {

    @ExceptionHandler(PsException.class)
    public ResponseEntity<PsExceptionMessage> processPsException(PsException e) {
        log.error("Exception happened", e);

        HttpStatus status;
        PsErrorCode errorCode = e.getErrorCode();
        switch (errorCode) {
            case NOT_REGISTERED:
                status = HttpStatus.NOT_ACCEPTABLE;
                break;

            case AUTH_ATTEMPTS_EXCEEDED:
                status = HttpStatus.FORBIDDEN;
                break;

            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        PsExceptionMessage msg = new PsExceptionMessage(
                DateTime.now(),
                errorCode,
                e.getMessage()
        );
        return new ResponseEntity<>(msg, status);
    }

    @ExceptionHandler({HibernateException.class, PersistenceException.class, DatabaseException.class})
    public ResponseEntity<PsExceptionMessage> processDatabaseException(Exception e) {
        log.error("Exception happened", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        PsExceptionMessage msg = new PsExceptionMessage(
                DateTime.now(),
                PsErrorCode.DATABASE_OPERATION_FAILED,
                e.getMessage()
        );
        return new ResponseEntity<>(msg, status);
    }

    // -------------------------------------------------------------------------
    // Fall-back
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PsExceptionMessage> processException(Exception e) {
        log.error("Exception happened", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        PsExceptionMessage msg = new PsExceptionMessage(
                DateTime.now(),
                PsErrorCode.UNKNOWN_SERVER_ERROR,
                e.getMessage()
        );
        return new ResponseEntity<>(msg, status);
    }
}
