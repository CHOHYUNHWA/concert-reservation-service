package kr.hhplus.be.server.support.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        switch (e.getErrorType().getLogLevel()) {
            case ERROR:
                log.error("Business ERROR : {}, {}", e.getMessage(), e.getPayload(), e);
                break;
            case WARN:
                log.warn("Business WARN : {}, {}", e.getMessage(), e.getPayload(), e);
                break;
            default:
                log.info("Business INFO : {}, {}", e.getMessage(), e.getPayload(), e);
                break;
        }

        HttpStatus status;
        switch (e.getErrorType().getCode()) {
            case CLIENT_ERROR -> status = HttpStatus.BAD_REQUEST;
            case SERVER_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
            case TOKEN_ERROR -> status = HttpStatus.UNAUTHORIZED;
            case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
            default -> status = HttpStatus.OK;
        }
        return new ResponseEntity<>(ErrorResponse.of(e), status);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handle(MissingRequestHeaderException e) {
        log.error("토큰이 없습니다: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorType.NOT_EXIST_TOKEN,
                null // 사용자 친화적인 메시지
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}


