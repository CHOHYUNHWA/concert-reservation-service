package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.QueueStatus;
import kr.hhplus.be.server.support.type.ReservationStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrentPaymentTest {

    private final Long USER_ID = 1L;
    private Queue token;
    private Point point;
    private User user;
    private Concert concert;
    private Reservation reservation;
    private ConcertSchedule concertSchedule;
    private Seat seat;

    private final Logger log = Logger.getLogger(ConcurrentPaymentTest.class.getName());

    @Autowired
    private QueueJpaRepository queueJpaRepository;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PointService pointService;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private QueueService queueService;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Test
    void 사용자가_동시에_여러_번_결제를_요청하면_한_번만_성공한다() throws InterruptedException {
        // given
        Queue queue = queueService.createToken();
        token = queue;

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

        reservationJpaRepository.save(reservation);


        pointService.chargePoint(user.getId(), 100_000L);

        // when
        AtomicInteger successCnt = new AtomicInteger(0);
        AtomicInteger failCnt = new AtomicInteger(0);

        final int threadCount = 5;
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {

                log.info(token.getStatus().toString());

                try {
                    paymentFacade.payment(token.getToken(), reservation.getId(), user.getId());
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Thread.sleep(1000);

        // 결제 요청이 한 번만 수행됐는지 검증한다.
        assertThat(successCnt.intValue()).isOne();
        // 실패한 횟수가 threadCount 에서 성공한 횟수를 뺀 값과 같은지 검증한다.
        assertThat(failCnt.intValue()).isEqualTo(threadCount - successCnt.intValue());
    }
}
