package kr.hhplus.be.server.domain.entity;

import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationTest {


    @Test
    void 예약_유효성_검증_시_사용자ID와_예약자_ID가_다른경우_예외_처리(){
        //given
        Long wrongUserId = 3L;
        Reservation reservation = Reservation.builder()
                .concertId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .userId(1L)
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now())
                .build();

        //when //then
        assertThatThrownBy(() -> reservation.validateReservation(wrongUserId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.PAYMENT_USER_MISMATCH.getMessage());
    }

    @Test
    void 예약_유효성_검증_시_결제_대기_시간이_5분_초과시_예외_처리(){
        //given
        Long userId = 1L;
        Reservation reservation = Reservation.builder()
                .concertId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .userId(userId)
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now().minusMinutes(6))
                .build();

        //when //then
        assertThatThrownBy(() -> reservation.validateReservation(userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.PAYMENT_TIMEOUT.getMessage());
    }

    @Test
    void 예약_유효성_검증_시_좌석을_예매할수_없는_상태의_경우_다른경우_예외_처리(){
        //given
        Long userId = 1L;
        Reservation reservation = Reservation.builder()
                .concertId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .userId(userId)
                .status(ReservationStatus.PAYMENT_COMPLETED)
                .reservedAt(LocalDateTime.now())
                .build();

        //when //then
        assertThatThrownBy(() -> reservation.validateReservation(userId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.ALREADY_PAID.getMessage());
    }

    @Test
    void 정상적으로_예약완료_상태_변경(){
        //given
        Long userId = 1L;
        Reservation reservation = Reservation.builder()
                .concertId(1L)
                .concertScheduleId(1L)
                .seatId(1L)
                .userId(userId)
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now())
                .build();

        //when
        Reservation reservedReservation = reservation.changeCompletedStatus();

        //then
        assertThat(reservedReservation.getStatus()).isEqualTo(ReservationStatus.PAYMENT_COMPLETED);
    }

}
