package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.domain.service.ConcertService;
import kr.hhplus.be.server.domain.service.QueueService;
import kr.hhplus.be.server.infra.repository.jpa.*;
import kr.hhplus.be.server.interfaces.dto.reservation.ReservationHttpDto;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ConcurrentReservationTest {

    private final Long USER_ID = 1L;
    private Queue token;
    private Point point;
    private User user;
    private Concert concert;
    private Reservation reservation;
    private ConcertSchedule concertSchedule;
    private Seat seat;

    private final Logger log = LoggerFactory.getLogger(ConcurrentReservationTest.class);

    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private QueueService queueService;
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
    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private ConcertService concertService;


    @Test
    @Transactional
    void 다수의_사용자가_1개의_좌석을_동시에_예약하면_한_명만_성공한다() throws InterruptedException {
        // when
        final int threadCount = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (long l = 1; l <= threadCount; l++) {

            ReservationHttpDto.ReservationRequest reservationRequest = ReservationHttpDto.ReservationRequest.builder()
                    .userId(l)
                    .concertId(1L)
                    .concertScheduleId(1L)
                    .seatId(1L)
                    .build();


            executorService.submit(() -> {
                Queue queue = queueService.createToken();

                try {
                    reservationFacade.reservation(reservationRequest);
                } catch (Exception e) {
                } finally {
                    countDownLatch.countDown();
                }
            });

        }
        countDownLatch.await();

        List<Reservation> reservations = reservationRepository.findByConcertIdAndConcertScheduleIdAndSeatId(1L, 1L, 1L);
        assertThat(reservations.size()).isOne();
        assertThat(concertRepository.findSeatByIdWithLock(1L).getSeatStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }

}
