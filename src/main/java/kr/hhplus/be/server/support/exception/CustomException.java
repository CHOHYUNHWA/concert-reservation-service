package kr.hhplus.be.server.support.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorType errorType;
    private final Object payload;

    public CustomException(ErrorType errorType, Object payload) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.payload = payload;
    }

}
