package kr.hhplus.be.server.support.exception;

import lombok.Builder;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Builder
public class ErrorResponse {
    private int status;
    private String errorCode;
    private String message;
    private Object payload;

    public static ErrorResponse of(CustomException e) {
        return ErrorResponse.builder()
                .errorCode(e.getErrorType().getCode().name())
                .message(e.getErrorType().getMessage())
                .payload(e.getPayload())
                .build();
    }

    public static ErrorResponse of(ErrorType errorType, String message){
        return ErrorResponse.builder()
                .errorCode(errorType.getCode().name())
                .message(errorType.getMessage())
                .payload(message)
                .build();
    }


}
