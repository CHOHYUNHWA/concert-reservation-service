package kr.hhplus.be.server.support.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생하였습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰 입니다." ),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"없는 토큰 입니다." ),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "없는 유저 입니다." ),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 포인트 값 입니다." ),
    BEFORE_AVAILABLE_RESERVATION_AT(HttpStatus.BAD_REQUEST,"예약 가능시간 전 입니다."),
    ALREADY_CONCERT_START(HttpStatus.BAD_REQUEST,"이미 시작한 콘서트 입니다." ),
    ALREADY_RESERVED_SEAT(HttpStatus.BAD_REQUEST, "이미 예약된 좌석입니다." ),
    ALREADY_CONCERT_DONE(HttpStatus.BAD_REQUEST, "이미 종료된 콘서트 입니다."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "없는 콘서트 입니다."),
    CONCERT_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"없는 콘서트 시간 입니다." ),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND,"없는 좌석 입니다." ),
    PAYMENT_USER_MISMATCH(HttpStatus.BAD_REQUEST,"예약자가 일치하지 않습니다." ),
    PAYMENT_TIMEOUT(HttpStatus.REQUEST_TIMEOUT,"결제요청 시간이 초과하였습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
    ALREADY_PAID(HttpStatus.BAD_REQUEST,"이미 결제된 예약 입니다." );

    private final HttpStatus httpStatus;
    private final String message;

}
