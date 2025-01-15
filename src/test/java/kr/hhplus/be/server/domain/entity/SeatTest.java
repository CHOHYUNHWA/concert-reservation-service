package kr.hhplus.be.server.domain.entity;

import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SeatTest {


    @Test
    void 좌석의_상태를_확인_NOT_AVAILABLE인_경우_예외_발생(){
        //given
        Seat seat = Seat.builder()
                .id(1L)
                .concertScheduleId(1L)
                .seatNumber(1L)
                .seatPrice(10000L)
                .seatStatus(SeatStatus.UNAVAILABLE)
                .reservedAt(LocalDateTime.now())
                .build();

        //when //then
        assertThatThrownBy(seat::checkStatus)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.ALREADY_RESERVED_SEAT.getMessage());
    }

    @Test
    void 콘서트_좌석_배정_성공(){
        //given
        SeatStatus expectedSeatStatus = SeatStatus.UNAVAILABLE;

        Seat seat = Seat.builder()
                .id(1L)
                .concertScheduleId(1L)
                .seatNumber(1L)
                .seatPrice(10000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .build();

        //when
        Seat assignedSeat = seat.assign();

        // then
        assertThat(assignedSeat).isNotNull();
        assertThat(assignedSeat.getSeatStatus()).isEqualTo(expectedSeatStatus);
        assertThat(assignedSeat.getReservedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }
}
