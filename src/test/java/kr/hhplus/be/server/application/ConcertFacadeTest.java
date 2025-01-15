package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.ConcertFacade;
import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.concert.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.QueueStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class ConcertFacadeTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;


    private User savedUser;
    private Concert savedConcert;
    private ConcertSchedule savedConcertSchedule;
    private Seat savedSeat;

    @BeforeEach
    void setUp(){
        User user = User.builder()
                .name("테스트")
                .build();
        savedUser = userJpaRepository.save(user);

        Concert concert = Concert.builder()
                .title("콘서트")
                .description("내용")
                .status(ConcertStatus.OPEN)
                .build();

        savedConcert = concertJpaRepository.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusMinutes(10))
                .concertTime(LocalDateTime.now().plusMinutes(20))
                .build();
        savedConcertSchedule = concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .concertScheduleId(concertSchedule.getId())
                .seatNumber(1L)
                .seatPrice(10000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .build();
        savedSeat = seatJpaRepository.save(seat);


    }

    @Test
    @Transactional
    void 전체_콘서트_조회_성공(){
        //given

        //when //then
        assertDoesNotThrow(() -> concertFacade.getConcerts());
    }

    @Test
    @Transactional
    void 예약_가능한_콘서트_스케쥴_조회_성공(){
        //given

        //when
        ConcertHttpDto.AvailableReservationConcertDateResponse result = concertFacade.getConcertSchedules(savedConcert.getId());
        List<ConcertHttpDto.ScheduleDto> concertSchedules = result.getSchedules();

        //then
        LocalDateTime now = LocalDateTime.now();
        assertThat(result.getConcertId()).isEqualTo(savedConcert.getId());
        for (ConcertHttpDto.ScheduleDto concertSchedule : concertSchedules) {
            assertThat(concertSchedule.getConcertTime()).isAfter(now);
            assertThat(concertSchedule.getAvailableReservationTime()).isBefore(now);
        }

    }

    @Test
    @Transactional
    void 예약_가능한_좌석_조회_성공(){
        //given

        //when
        ConcertHttpDto.AvailableReservationConcertSeatResponse concertSeats = concertFacade.getConcertSeats(savedConcert.getId(), savedConcertSchedule.getId());
        List<ConcertHttpDto.SeatDto> seats = concertSeats.getSeats();

        //then
        assertThat(concertSeats.getConcertId()).isEqualTo(savedConcert.getId());
        for (ConcertHttpDto.SeatDto seat : seats) {
            assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
        }
    }
}
