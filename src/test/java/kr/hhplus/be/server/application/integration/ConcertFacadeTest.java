package kr.hhplus.be.server.application.integration;

import kr.hhplus.be.server.application.facade.ConcertFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.concert.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class ConcertFacadeTest {

    private Concert concert;
    private ConcertSchedule concertSchedule;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @BeforeEach
    void setUp(){

        databaseCleanUp.execute();

        concert = Concert.builder()
                .title("콘서트")
                .description("내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusMinutes(10))
                .concertTime(LocalDateTime.now().plusMinutes(20))
                .build();
        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
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
}
