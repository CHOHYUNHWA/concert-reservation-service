package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.Concert;
import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

    private Concert concert;

    @BeforeEach
    void setUp() {
        concert = Concert.builder()
                .id(1L)
                .title("콘서트")
                .description("콘서트 설명")
                .status(ConcertStatus.OPEN)
                .build();
    }


    @Test
    void 콘서트_리스트를_조회_성공(){
        //given
        given(concertRepository.findConcertsAll()).willReturn(List.of(concert));

        //when
        List<Concert> concerts = concertService.getConcerts();

        //then
        assertThat(concerts).hasSize(1);
        assertThat(concerts.get(0).getId()).isEqualTo(1L);
        verify(concertRepository, times(1)).findConcertsAll();
    }

    @Test
    void 예약_가능한_콘서트_스케쥴_조회_성공(){
        //given
        Long concertScheduleId = 1L;

        ConcertSchedule concertSchedule = ConcertSchedule
                .builder()
                .id(concertScheduleId)
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(10))
                .build();

        given(concertRepository.findConcertSchedulesAllByConcertId(concert.getId())).willReturn(List.of(concertSchedule));

        //when
        List<ConcertSchedule> concertSchedules = concertService.getConcertSchedules(concert);

        //then
        assertThat(concertSchedules).hasSize(1);
        assertThat(concertSchedules.get(0).getId()).isEqualTo(concertScheduleId);
        verify(concertRepository, times(1)).findConcertSchedulesAllByConcertId(concert.getId());

    }

    @Test
    void 예약_가능한_좌석_조회_성공(){
        //given
        Long concertScheduleId = 1L;

        Seat seat = Seat
                .builder()
                .id(1L)
                .concertScheduleId(1L)
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();

        given(concertRepository.findSeatsAllByConcertScheduleId(concertScheduleId)).willReturn(List.of(seat));

        //when
        List<Seat> seats = concertService.getSeats(concertScheduleId);

        //then
        assertThat(seats).hasSize(1);
        assertThat(seats.get(0).getId()).isEqualTo(concertScheduleId);
        verify(concertRepository, times(1)).findSeatsAllByConcertScheduleId(concertScheduleId);
    }


    @Test
    void 이미_시작한_콘서트에_대해_예약_가능_스케쥴_검증_시_예약이_불가능한_스케쥴일_경우_예외_던짐(){
        //given
        Long concertScheduleId = 1L;
        Long seatId = 1L;
        ConcertSchedule concertSchedule = ConcertSchedule
                .builder()
                .id(concertScheduleId)
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().minusDays(10))
                .build();

        Seat seat = Seat
                .builder()
                .id(seatId)
                .concertScheduleId(concertSchedule.getConcertId())
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();


        //when //then
        assertThatThrownBy(() -> concertService.isAvailableReservationSeat(concertSchedule, seat))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_CONCERT_START.getMessage());
    }

    @Test
    void 예약_시작_이전_콘서트에_대해_예약_가능_스케쥴_검증_시_예약이_불가능한_스케쥴일_경우_예외_던짐(){
        //given
        Long concertScheduleId = 1L;
        Long seatId = 1L;
        ConcertSchedule concertSchedule = ConcertSchedule
                .builder()
                .id(concertScheduleId)
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().plusDays(5))
                .concertTime(LocalDateTime.now().plusDays(10))
                .build();

        Seat seat = Seat
                .builder()
                .id(seatId)
                .concertScheduleId(concertSchedule.getConcertId())
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();


        //when //then
        assertThatThrownBy(() -> concertService.isAvailableReservationSeat(concertSchedule, seat))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BEFORE_AVAILABLE_RESERVATION_AT.getMessage());
    }


    @Test
    void 예약_가능_좌석_검증_시_예약이_불가능한_좌석일_경우_예외_던짐(){
        //given
        Long concertScheduleId = 1L;
        Long seatId = 1L;
        ConcertSchedule concertSchedule = ConcertSchedule
                .builder()
                .id(concertScheduleId)
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(10))
                .build();

        Seat seat = Seat
                .builder()
                .id(seatId)
                .concertScheduleId(concertSchedule.getConcertId())
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.UNAVAILABLE)
                .build();

        //when //then
        assertThatThrownBy(() -> concertService.isAvailableReservationSeat(concertSchedule, seat))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_RESERVED_SEAT.getMessage());
    }

}
