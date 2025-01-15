package kr.hhplus.be.server.support.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Server Error : {}", e.getMessage(), e);
        return new ResponseEntity<>(ErrorResponse.of(ErrorType.INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        switch (e.getErrorType().getLogLevel()){
            case ERROR: {
                log.error("Business ERROR : {}, {}", e.getMessage(), e.getPayload(), e);
            };
            case WARN: {
                log.warn("Business WARN : {}, {}", e.getMessage(), e.getPayload(), e);
            };
            default: {
                log.info("Business INFO : {}, {}", e.getMessage(), e.getPayload(), e);
            }
        }

        HttpStatus status;
        switch (e.getErrorType().getCode()){
            case CLIENT_ERROR -> status = HttpStatus.BAD_REQUEST;
            case SERVER_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
            case TOKEN_ERROR -> status = HttpStatus.UNAUTHORIZED;
            case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
            default -> status = HttpStatus.OK;
        }
        return new ResponseEntity<>(ErrorResponse.of(e), status);
    }
}
