package kr.hhplus.be.server.application.integration.concurrent;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.service.PointService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.ReservationStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrentPaymentTest {

    private Queue token;
    private User user;
    private Reservation reservation;

    private final int threadCount = 30;

    //데이터 비우기
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    //주요 비즈니스 테스트 대상
    @Autowired
    private PaymentFacade paymentFacade;

    //사전 객체 생성 요소
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private PointService pointService;
    @Autowired
    private QueueService queueService;

    @BeforeEach
    void setUp(){
        databaseCleanUp.execute();

        token = queueService.createToken();

        user = User.builder()
                .name("유저")
                .build();

        userJpaRepository.save(user);

        Point point = Point.builder()
                .userId(user.getId())
                .amount(0L)
                .build();

        pointJpaRepository.save(point);

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


        pointService.chargePointWithoutLock(user.getId(), 100_000L);


    }

    @Test
    void 낙관적락_사용자가_동시에_여러_번_결제를_요청하면_한_번만_성공한다() throws InterruptedException {
        // given

        // when
        AtomicInteger successCnt = new AtomicInteger(0);
        AtomicInteger failCnt = new AtomicInteger(0);

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.paymentWithOptimisticLock(token.getToken(), reservation.getId(), user.getId());
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }


        countDownLatch.await();
        // 결제 요청이 한번만 성공했는지 검증
        assertThat(successCnt.intValue()).isOne();
        // 실패한 횟수가 threadCount 에서 성공한 횟수를 뺀 값과 같은지 검증
        assertThat(failCnt.intValue()).isEqualTo(threadCount - successCnt.intValue());
    }


    @Test
    void 비관적락_사용자가_동시에_여러_번_결제를_요청하면_한_번만_성공한다() throws InterruptedException {
        // given

        // when
        AtomicInteger successCnt = new AtomicInteger(0);
        AtomicInteger failCnt = new AtomicInteger(0);

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.paymentWithPessimisticLock(token.getToken(), reservation.getId(), user.getId());
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }


        countDownLatch.await();
        // 결제 요청이 한번만 성공했는지 검증
        assertThat(successCnt.intValue()).isOne();
        // 실패한 횟수가 threadCount 에서 성공한 횟수를 뺀 값과 같은지 검증
        assertThat(failCnt.intValue()).isEqualTo(threadCount - successCnt.intValue());
    }


    @Test
    void Redis_분산락_사용자가_동시에_여러_번_결제를_요청하면_한_번만_성공한다() throws InterruptedException {
        // given

        // when
        AtomicInteger successCnt = new AtomicInteger(0);
        AtomicInteger failCnt = new AtomicInteger(0);

        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.paymentWithDistributedLock(token.getToken(), reservation.getId(), user.getId());
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    failCnt.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }


        countDownLatch.await();
        // 결제 요청이 한번만 성공했는지 검증
        assertThat(successCnt.intValue()).isOne();
        // 실패한 횟수가 threadCount 에서 성공한 횟수를 뺀 값과 같은지 검증
        assertThat(failCnt.intValue()).isEqualTo(threadCount - successCnt.intValue());
    }

}
