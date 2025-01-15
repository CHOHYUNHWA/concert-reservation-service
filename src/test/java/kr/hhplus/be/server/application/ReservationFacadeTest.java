package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.domain.repository.QueueRepository;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.domain.service.ConcertService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.domain.service.ReservationService;
import kr.hhplus.be.server.infra.repository.jpa.ConcertJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.SeatJpaRepository;
import kr.hhplus.be.server.infra.repository.jpa.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.reservation.ReservationHttpDto;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorType;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.ReservationStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ReservationFacadeTest {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private String token;
    private final Long USER_ID = 1L;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @BeforeEach
    void setUp() {
        Queue queue = queueService.createToken();
        token = queue.getToken(); // 토큰 검증 통과를 위한 토큰 생성
    }


    @Test
    void 예약_가능_시간_이전_예약_요청_시_예외를_반환() {
        // given
        User user = User.builder()
                .name("이름")
                .build();

        userJpaRepository.save(user);

        Concert concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().plusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);


        ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                .userId(USER_ID)
                .concertId(concert.getId())
                .concertScheduleId(concertSchedule.getId())
                .seatId(seat.getId())
                .build();


        // when & then
        assertThatThrownBy(() -> reservationFacade.reservation(reservationRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.BEFORE_AVAILABLE_RESERVATION_AT.getMessage());

    }

    @Test
    void 예약_마감_시간_이후_예약_요청_시_예외를_반환() {
        // given
        User user = User.builder()
                .name("이름")
                .build();

        userJpaRepository.save(user);

        Concert concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().minusMinutes(1L))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);


        ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                .userId(USER_ID)
                .concertId(concert.getId())
                .concertScheduleId(concertSchedule.getId())
                .seatId(seat.getId())
                .build();


        // when & then
        assertThatThrownBy(() -> reservationFacade.reservation(reservationRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.ALREADY_CONCERT_START.getMessage());


    }

    @Test
    void 좌석의_상태가_UNAVAILABLE_이라면_예외를_반환() {
        // given
        User user = User.builder()
                .name("이름")
                .build();

        userJpaRepository.save(user);

        Concert concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.UNAVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);


        ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                .userId(USER_ID)
                .concertId(concert.getId())
                .concertScheduleId(concertSchedule.getId())
                .seatId(seat.getId())
                .build();


        // when & then
        assertThatThrownBy(() -> reservationFacade.reservation(reservationRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.ALREADY_RESERVED_SEAT.getMessage());

    }

    @Test
    void 예약처리_정상_성공() {
        // given
        User user = User.builder()
                .name("이름")
                .build();

        userJpaRepository.save(user);

        Concert concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);


        ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                .userId(USER_ID)
                .concertId(concert.getId())
                .concertScheduleId(concertSchedule.getId())
                .seatId(seat.getId())
                .build();

        // when
        ReservationHttpDto.ReservationCompletedResponse reservation = reservationFacade.reservation(reservationRequest);

        //then
        assertThat(reservation).isNotNull();
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.PAYMENT_WAITING);

    }
}
