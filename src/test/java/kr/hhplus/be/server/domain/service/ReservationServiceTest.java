package kr.hhplus.be.server.domain.service;


import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.support.type.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    void 예약_생성_성공(){
        //given
        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        Seat seat = mock(Seat.class);
        Reservation reservation = Reservation.create(concertSchedule, 1L, 1L);

        // 수정: Argument Matchers 사용
        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);

        //when
        Reservation result = reservationService.createReservation(concertSchedule, seat, 1L);

        //then
        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void 예약_유효성_검증_성공(){
        //given
        Long userId = 1L;
        Long seatId = 1L;
        Long concertId = 1L;
        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        Reservation reservation =
                Reservation.builder()
                        .id(1L)
                        .concertId(concertId)
                        .concertScheduleId(1L)
                        .userId(userId)
                        .seatId(seatId)
                        .reservedAt(LocalDateTime.now())
                        .status(ReservationStatus.PAYMENT_WAITING)
                        .build();

        given(reservationRepository.findById(reservation.getId())).willReturn(reservation);

        //when
        Reservation result = reservationService.validateReservation(reservation.getId(), userId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reservation.getId());
        assertThat(result.getUserId()).isEqualTo(reservation.getUserId());
    }

    @Test
    void 예약_완료_변경_성공(){
        //given
        Long userId = 1L;
        Long seatId = 1L;
        Long concertId = 1L;
        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        Reservation reservation =
                Reservation.builder()
                        .id(1L)
                        .concertId(concertId)
                        .concertScheduleId(1L)
                        .userId(userId)
                        .seatId(seatId)
                        .reservedAt(LocalDateTime.now())
                        .status(ReservationStatus.PAYMENT_COMPLETED)
                        .build();

        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);

        //when
        Reservation result = reservationService.changeCompletedStatus(reservation);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PAYMENT_COMPLETED);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void 예약_조회_성공(){
        Long userId = 1L;
        Long seatId = 1L;
        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        Reservation reservation = Reservation.create(concertSchedule, seatId, userId);
        given(reservationRepository.findByUserId(userId)).willReturn(reservation);


        //when
        Reservation result = reservationService.getReservationByUserId(userId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reservation.getId());
        assertThat(result.getUserId()).isEqualTo(reservation.getUserId());
        verify(reservationRepository, times(1)).findByUserId(userId);
    }
}
