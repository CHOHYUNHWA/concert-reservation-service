package kr.hhplus.be.server.application.integration.concurrent;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.reservation.ReservationHttpDto;
import kr.hhplus.be.server.support.type.ConcertStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import kr.hhplus.be.server.util.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrentReservationTest {

    private Concert concert;
    private ConcertSchedule concertSchedule;
    private Seat seat;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();

        for(long l = 1; l <= 5; l++ ) {
            User user = User.builder()
                    .name("TEST")
                    .build();
            userJpaRepository.save(user);

            Point point = Point.builder()
                    .userId(user.getId())
                    .amount(0L)
                    .build();
            pointJpaRepository.save(point);
        }

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
    }


    @Test
    void 비관적_락_다수의_사용자가_1개의_좌석을_동시에_예약하면_한_명만_성공한다() throws InterruptedException {
        // when
        final int threadCount = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (long l = 1; l <= threadCount; l++) {

            ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                    .userId(l)
                    .concertId(concert.getId())
                    .concertScheduleId(concertSchedule.getId())
                    .seatId(seat.getId())
                    .build();


            executorService.submit(() -> {
                try {
                    reservationFacade.reservationWithPessimisticLock(reservationRequest);
                } catch (Exception e) {
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        List<Reservation> reservations = reservationRepository.findByConcertIdAndConcertScheduleIdAndSeatId(concert.getId(), concertSchedule.getId(), seat.getId());
        assertThat(reservations.size()).isOne();
        assertThat(concertRepository.findBySeatId(seat.getId()).getSeatStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }

    @Test
    void 낙관적_락_다수의_사용자가_1개의_좌석을_동시에_예약하면_한_명만_성공한다() throws InterruptedException {
        // when
        final int threadCount = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (long l = 1; l <= threadCount; l++) {

            ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                    .userId(l)
                    .concertId(concert.getId())
                    .concertScheduleId(concertSchedule.getId())
                    .seatId(seat.getId())
                    .build();


            executorService.submit(() -> {
                try {
                    reservationFacade.reservationWithOptimisticLock(reservationRequest);
                } catch (Exception e) {
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        List<Reservation> reservations = reservationRepository.findByConcertIdAndConcertScheduleIdAndSeatId(concert.getId(), concertSchedule.getId(), seat.getId());
        assertThat(reservations.size()).isOne();
        assertThat(concertRepository.findBySeatId(seat.getId()).getSeatStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }



    @Test
    void Redis_분산락_다수의_사용자가_1개의_좌석을_동시에_예약하면_한_명만_성공한다() throws InterruptedException {
        // when
        final int threadCount = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (long l = 1; l <= threadCount; l++) {

            ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                    .userId(l)
                    .concertId(concert.getId())
                    .concertScheduleId(concertSchedule.getId())
                    .seatId(seat.getId())
                    .build();


            executorService.submit(() -> {
                try {
                    reservationFacade.reservationWithDistributedLock(reservationRequest);
                } catch (Exception e) {
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        List<Reservation> reservations = reservationRepository.findByConcertIdAndConcertScheduleIdAndSeatId(concert.getId(), concertSchedule.getId(), seat.getId());
        assertThat(reservations.size()).isOne();
        assertThat(concertRepository.findBySeatId(seat.getId()).getSeatStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }

}
