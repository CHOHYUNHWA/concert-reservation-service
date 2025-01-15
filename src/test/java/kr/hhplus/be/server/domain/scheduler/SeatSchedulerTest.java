package kr.hhplus.be.server.domain.scheduler;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.*;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.interfaces.scheduler.SeatScheduler;
import kr.hhplus.be.server.support.type.ReservationStatus;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SeatSchedulerTest {

    @Autowired
    private SeatScheduler seatScheduler;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void 예약후_5분_이상_지났지만_결제되지_않은_경우_좌석을_이용_가능_상태로_변경한다() {
        //given
        Seat seat = Seat.builder()
                .concertScheduleId(1L)
                .seatNumber(1L)
                .seatStatus(SeatStatus.UNAVAILABLE)
                .reservedAt(LocalDateTime.now().minusMinutes(6))
                .seatPrice(5000L)
                .build();

        Seat savedSeat = concertRepository.saveSeat(seat);

        Reservation reservation = Reservation.builder()
                .concertId(1L)
                .concertScheduleId(1L)
                .seatId(savedSeat.getId())
                .userId(1L)
                .status(ReservationStatus.PAYMENT_WAITING)
                .reservedAt(LocalDateTime.now().minusMinutes(6))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // when
        seatScheduler.changeAvailableSeat();

        // then
        Seat updatedSeat = concertRepository.findSeatByIdWithLock(savedSeat.getId());
        assertThat(updatedSeat.getSeatStatus()).isEqualTo(SeatStatus.AVAILABLE);

        Reservation updatedReservation = reservationRepository.findByIdWithLock(savedReservation.getId());
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }
}

