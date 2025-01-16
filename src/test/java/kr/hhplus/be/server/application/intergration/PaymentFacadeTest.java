package kr.hhplus.be.server.application.intergration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.domain.repository.UserRepository;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentHttpDto;
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
public class PaymentFacadeTest {

    private User user;
    private Point point;
    private Concert concert;
    private ConcertSchedule concertSchedule;
    private Seat seat;
    private Reservation reservation;
    private String token;

    @Autowired
    private QueueService queueService;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @BeforeEach
    void setUp() {
        Queue queue = queueService.createToken();
        token = queue.getToken();

        user = User.builder()
                .name("유저")
                .build();

        userJpaRepository.save(user);

        point = Point.builder()
                .userId(user.getId())
                .amount(0L)
                .build();

        pointJpaRepository.save(point);

        concert = Concert.builder()
                .title("콘서트")
                .description("콘서트내용")
                .status(ConcertStatus.OPEN)
                .build();

        concertJpaRepository.save(concert);

        concertSchedule = ConcertSchedule.builder()
                .concertId(concert.getId())
                .availableReservationTime(LocalDateTime.now().minusDays(5))
                .concertTime(LocalDateTime.now().plusDays(30))
                .build();

        concertScheduleJpaRepository.save(concertSchedule);

        seat = Seat.builder()
                .seatNumber(1L)
                .seatPrice(100L)
                .seatStatus(SeatStatus.AVAILABLE)
                .reservedAt(null)
                .concertScheduleId(concertSchedule.getId())
                .build();

       seatJpaRepository.save(seat);

        reservation = Reservation.builder()
                .concertScheduleId(concertSchedule.getId())
                .userId(user.getId())
                .concertId(concert.getId())
                .seatId(seat.getId())
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now())
                .build();

        reservationRepository.save(reservation);
    }

    @Test
    @Transactional
    void 결제_성공(){
        //given
        pointService.chargePoint(user.getId(), 10_000L);

        //when
        PaymentHttpDto.PaymentCompletedResponse payment = paymentFacade.payment(token,reservation.getId(), user.getId());
        //then
        assertThat(payment).isNotNull();
        assertThat(payment.getAmount()).isEqualTo(seat.getSeatPrice());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAYMENT_COMPLETED);
    }

    @Test
    @Transactional
    void 잔액_부족시_결제_예외_반환(){
        //when then
        assertThatThrownBy(() -> paymentFacade.payment(token, reservation.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.INVALID_AMOUNT.getMessage());

    }

    @Test
    @Transactional
    void 예약자_결제자_불_일치_시_예외_반환(){
        assertThatThrownBy(() -> paymentFacade.payment(token, reservation.getId(), 2L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.PAYMENT_USER_MISMATCH.getMessage());
    }

    @Test
    @Transactional
    void 임시_예약_후_5분_경과시_예외_반환(){
        //given
        pointService.chargePoint(user.getId(), 10_000L);
        Reservation timeOutReservation = Reservation.builder()
                .concertId(concert.getId())
                .concertScheduleId(concertSchedule.getId())
                .seatId(seat.getId())
                .userId(user.getId())
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now().minusMinutes(6))
                .build();

        Reservation savedTimeoutReservation = reservationRepository.save(timeOutReservation);

        //when //then
        assertThatThrownBy(() -> paymentFacade.payment(token, savedTimeoutReservation.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.PAYMENT_TIMEOUT.getMessage());
    }

    @Test
    @Transactional
    void 이미_결제된_예약의_경우_예외_반환(){
        //given
        pointService.chargePoint(user.getId(), 10_000L);
        Reservation alreadyPaymentCompletedReservation = Reservation.builder()
                .concertId(concert.getId())
                .concertScheduleId(concertSchedule.getId())
                .seatId(seat.getId())
                .userId(user.getId())
                .status(ReservationStatus.PAYMENT_COMPLETED)
                .reservedAt(LocalDateTime.now())
                .build();

        Reservation savedAlreadyPaymentCompletedReservation = reservationRepository.save(alreadyPaymentCompletedReservation);

        //when //then
        assertThatThrownBy(() -> paymentFacade.payment(token, savedAlreadyPaymentCompletedReservation.getId(), user.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorType.ALREADY_PAID.getMessage());

    }
}
