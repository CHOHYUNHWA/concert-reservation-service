package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.ConcertFacade;
import kr.hhplus.be.server.application.facade.QueueFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.*;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import kr.hhplus.be.server.support.type.ConcertStatus;
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
    private QueueFacade queueFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private QueueJpaRepository queueJpaRepository;

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
    void 토큰이_유효하면_전체_콘서트_조회_성공(){
        //given
        Queue queue = queueFacade.createToken(savedUser.getId());
        String token = queue.getToken();

        //when
        List<AvailableReservationConcertResponse> concerts = concertFacade.getConcerts(token);

        //then
        assertDoesNotThrow(() -> concertFacade.getConcerts(token));

    }

    @Test
    @Transactional
    void 토큰이_유효하지_않으면_콘서트_조회_실패(){
        //given
        Queue queue = queueFacade.createToken(savedUser.getId());
        queue.expiredToken();

        //when //then
        assertThatThrownBy(() -> concertFacade.getConcerts(queue.getToken()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED.getMessage());
    }

    @Test
    @Transactional
    void 예약_가능한_콘서트_스케쥴_조회_성공(){
        //given
        Queue queue = queueFacade.createToken(savedUser.getId());

        //when
        AvailableReservationConcertDateResponse result = concertFacade.getConcertSchedules(queue.getToken(), savedConcert.getId());
        List<ScheduleDto> concertSchedules = result.getSchedules();

        //then
        LocalDateTime now = LocalDateTime.now();
        assertThat(result.getConcertId()).isEqualTo(savedConcert.getId());
        for (ScheduleDto concertSchedule : concertSchedules) {
            assertThat(concertSchedule.getConcertTime()).isAfter(now);
            assertThat(concertSchedule.getAvailableReservationTime()).isBefore(now);
        }

    }

    @Test
    @Transactional
    void 예약_가능한_좌석_조회_성공(){
        //given
        Queue queue = queueFacade.createToken(savedUser.getId());

        //when
        AvailableReservationConcertSeatResponse concertSeats = concertFacade.getConcertSeats(queue.getToken(), savedConcert.getId(), savedConcertSchedule.getId());
        List<SeatDto> seats = concertSeats.getSeats();

        //then
        assertThat(concertSeats.getConcertId()).isEqualTo(savedConcert.getId());
        for (SeatDto seat : seats) {
            assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
        }
    }
}
