package kr.hhplus.be.server.domain.entity;


import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConcertScheduleTest {

    @Test
    void 예약시간_마감_시간_이전_콘서트_스케쥴_상태_조회시_예외_반환(){
        //given
        ConcertSchedule concertSchedule = ConcertSchedule
                .builder()
                .id(1L)
                .concertId(1L)
                .availableReservationTime(LocalDateTime.now().plusMinutes(5))
                .concertTime(LocalDateTime.now().plusMinutes(10))
                .build();

        //when //then
        assertThatThrownBy(concertSchedule::checkStatus)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BEFORE_AVAILABLE_RESERVATION_AT.getMessage());
    }

    @Test
    void 콘서트_시작_시간_이후_콘서트_스케쥴_상태_조회시_예외_반환(){
        //given
        ConcertSchedule concertSchedule = ConcertSchedule
                .builder()
                .id(1L)
                .concertId(1L)
                .availableReservationTime(LocalDateTime.now().minusMinutes(10))
                .concertTime(LocalDateTime.now().minusMinutes(5))
                .build();

        //when //then
        assertThatThrownBy(concertSchedule::checkStatus)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_CONCERT_START.getMessage());
    }
}
