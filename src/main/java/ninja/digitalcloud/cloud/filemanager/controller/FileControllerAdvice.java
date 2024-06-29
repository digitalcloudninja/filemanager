package ninja.digitalcloud.cloud.filemanager.controller;

import ninja.digitalcloud.cloud.filemanager.exception.BadRequestError;
import ninja.digitalcloud.cloud.filemanager.exception.FileNotFoundError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FileControllerAdvice extends ResponseEntityExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(FileControllerAdvice.class);

    @ExceptionHandler(BadRequestError.class)
    public ResponseEntity<Object> handleBadRequestError(Exception exception, WebRequest request) {
        logger.error(String.format("BadRequest %s message=%s", request.getDescription(false), exception.getLocalizedMessage()));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(FileNotFoundError.class)
    public ResponseEntity<Object> handleFileNotFoundError(Exception exception, WebRequest request) {
        logger.error(String.format("FileNotFound %s message=%s", request.getDescription(false), exception.getLocalizedMessage()));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(value = {RuntimeException.class, Exception.class})
    public ResponseEntity<Object> handleRuntimeExceptions(Exception exception, WebRequest request) {
        logger.error(String.format("Unhandled %s message=%s", request.getDescription(false), exception.getLocalizedMessage()));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }
}
