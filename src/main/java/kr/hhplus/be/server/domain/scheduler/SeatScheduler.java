package kr.hhplus.be.server.domain.scheduler;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private final ConcertRepository concertRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void changeAvailableSeat(){
        List<Reservation> notPaidReservations =
                concertRepository.findExpiredReservation(ReservationStatus.PAYMENT_WAITING, LocalDateTime.now().minusMinutes(5));

        for (Reservation notPaidReservation : notPaidReservations) {
            Seat seat = concertRepository.findSeatByIdWithLock(notPaidReservation.getSeatId());

            Seat availableUpdateSeat = seat.toAvailable();
            concertRepository.saveSeat(availableUpdateSeat);

            Reservation expiredReservation = notPaidReservation.changeExpiredStatus();
            reservationRepository.save(expiredReservation);
        }
    }

}
