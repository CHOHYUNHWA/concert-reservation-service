package kr.hhplus.be.server.application.integration;

import kr.hhplus.be.server.application.facade.ConcertFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.concert.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import kr.hhplus.be.server.util.CacheCleanUp;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ConcertFacadeTest {

    private Concert concert;
    private ConcertSchedule concertSchedule;
    private Seat seat;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private CacheCleanUp cacheCleanUp;

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @MockitoSpyBean
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp(){

        databaseCleanUp.execute();
        cacheCleanUp.clearAllCaches();

        concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusMinutes(10))
                .concertTime(LocalDateTime.now().plusMinutes(20))
                .build();
        concertScheduleJpaRepository.save(concertSchedule);

        seat = Seat.builder()
                .concertScheduleId(concertSchedule.getId())
                .seatNumber(1L)
                .seatPrice(10000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();
        seatJpaRepository.save(seat);


    }

    @Test
    void 전체_콘서트_조회_성공(){
        //given

        //when //then
        assertDoesNotThrow(() -> concertFacade.getConcerts());
    }

    @Test
    void 예약_가능한_콘서트_스케쥴_조회_성공(){
        //given

        //when
        ConcertHttpDto.AvailableReservationConcertDateResponse result = concertFacade.getConcertSchedules(concert.getId());
        List<ConcertHttpDto.ScheduleDto> concertSchedules = result.getSchedules();

        //then
        LocalDateTime now = LocalDateTime.now();
        assertThat(result.getConcertId()).isEqualTo(concert.getId());
        for (ConcertHttpDto.ScheduleDto concertSchedule : concertSchedules) {
            assertThat(concertSchedule.getConcertTime()).isAfter(now);
            assertThat(concertSchedule.getAvailableReservationTime()).isBefore(now);
        }

    }

    @Test
    void 예약_가능한_좌석_조회_성공(){
        //given

        //when
        ConcertHttpDto.AvailableReservationConcertSeatResponse concertSeats = concertFacade.getConcertSeats(concert.getId(), concertSchedule.getId());
        List<ConcertHttpDto.SeatDto> seats = concertSeats.getSeats();

        //then
        assertThat(concertSeats.getConcertId()).isEqualTo(concert.getId());
        for (ConcertHttpDto.SeatDto seat : seats) {
            assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
        }
    }

    @Test
    void 동일한_콘서트_수초내_여러번_조회_시_두번째_응답은_캐싱에서_데이터_조회_성공(){
        //given`

        doReturn(List.of(concert)).when(concertRepository).findConcertsAll();

        //when
        List<ConcertHttpDto.AvailableReservationConcertResponse> concerts1 = concertFacade.getConcerts();
        List<ConcertHttpDto.AvailableReservationConcertResponse> concerts2 = concertFacade.getConcerts();

        //then
        verify(concertRepository, times(1)).findConcertsAll();
    }

    @Test
    void 동일한_콘서트_스케쥴을_수초내_여러번_조회_시_두번째_응답은_캐싱에서_데이터_조회_성공(){
        //given
        doReturn(List.of(concertSchedule)).when(concertRepository).findConcertSchedulesAllByConcertId(concert.getId());

        //when
        ConcertHttpDto.AvailableReservationConcertDateResponse concertSchedules1 = concertFacade.getConcertSchedules(concert.getId());
        ConcertHttpDto.AvailableReservationConcertDateResponse concertSchedules2 = concertFacade.getConcertSchedules(concert.getId());

        //then
        verify(concertRepository, times(1)).findConcertSchedulesAllByConcertId(concert.getId());
    }

    @Test
    void 동일한_좌석_수초내_여러번_조회_시_두번쪠_응답은_캐싱에서_데이터_조회_성공(){
        //given
        doReturn(List.of(seat)).when(concertRepository).findSeatsAllByConcertScheduleId(concertSchedule.getId());

        //when
        ConcertHttpDto.AvailableReservationConcertSeatResponse concertSeats1 = concertFacade.getConcertSeats(concert.getId(), concertSchedule.getId());
        ConcertHttpDto.AvailableReservationConcertSeatResponse concertSeats2 = concertFacade.getConcertSeats(concert.getId(), concertSchedule.getId());

        //then
        verify(concertRepository, times(1)).findSeatsAllByConcertScheduleId(concertSchedule.getId());
    }
}
