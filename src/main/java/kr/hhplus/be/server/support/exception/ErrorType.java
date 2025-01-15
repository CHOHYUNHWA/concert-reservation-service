package kr.hhplus.be.server.support.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    //서버에러
    INTERNAL_SERVER_ERROR(ErrorCode.SERVER_ERROR, "서버 에러가 발생하였습니다.", LogLevel.ERROR),

    //JPA
    RESOURCE_NOT_FOUND(ErrorCode.NOT_FOUND, "리소스를 찾을 수 없습니다.", LogLevel.WARN ),

    //비즈니스
    INVALID_TOKEN(ErrorCode.TOKEN_ERROR, "유효하지 않는 토큰 입니다.",LogLevel.WARN),
    TOKEN_NOT_FOUND(ErrorCode.TOKEN_ERROR,"없는 토큰 입니다.", LogLevel.WARN),
    INVALID_AMOUNT(ErrorCode.BUSINESS_ERROR, "잘못된 포인트 값 입니다." , LogLevel.INFO),
    BEFORE_AVAILABLE_RESERVATION_AT(ErrorCode.BUSINESS_ERROR,"예약 가능시간 전 입니다.", LogLevel.INFO),
    ALREADY_CONCERT_START(ErrorCode.BUSINESS_ERROR,"이미 시작한 콘서트 입니다.", LogLevel.INFO ),
    ALREADY_RESERVED_SEAT(ErrorCode.BUSINESS_ERROR,"이미 예약된 좌석입니다." , LogLevel.INFO),
    ALREADY_CONCERT_DONE(ErrorCode.BUSINESS_ERROR, "이미 종료된 콘서트 입니다.", LogLevel.INFO),
    PAYMENT_USER_MISMATCH(ErrorCode.BUSINESS_ERROR,"예약자가 일치하지 않습니다." , LogLevel.INFO),
    PAYMENT_TIMEOUT(ErrorCode.BUSINESS_ERROR,"결제요청 시간이 초과하였습니다.", LogLevel.INFO),
    ALREADY_PAID(ErrorCode.BUSINESS_ERROR,"이미 결제된 예약 입니다." ,LogLevel.INFO);

    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;

}
