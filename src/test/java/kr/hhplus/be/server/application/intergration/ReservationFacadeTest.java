package kr.hhplus.be.server.application.intergration;

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
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ReservationFacadeTest {

    private User user;
    private Concert concert;

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();

        user = User.builder()
                .name("이름")
                .build();

        userJpaRepository.save(user);

        concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

    }


    @Test
    void 예약_가능_시간_이전_예약_요청_시_예외를_반환() {
        // given
        LocalDateTime beforeAvailableReservationTime = LocalDateTime.now().plusDays(5);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(beforeAvailableReservationTime)
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
                .userId(user.getId())
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
        LocalDateTime afterAvailableReservationTime = LocalDateTime.now().minusMinutes(1L);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(afterAvailableReservationTime)
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
                .userId(user.getId())
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
        SeatStatus unavailableSeatStatus = SeatStatus.UNAVAILABLE;

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        Seat seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100000L)
                .seatStatus(unavailableSeatStatus)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

        seatJpaRepository.save(seat);


        ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                .userId(user.getId())
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
                .userId(user.getId())
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
