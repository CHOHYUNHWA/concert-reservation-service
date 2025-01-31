package kr.hhplus.be.server.interfaces.scheduler;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ConcertRepository;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.support.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private final ConcertRepository concertRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void changeAvailableSeat(){
        List<Reservation> notPaidReservations =
                concertRepository.findExpiredReservation(ReservationStatus.PAYMENT_WAITING, LocalDateTime.now().minusMinutes(5));

        for (Reservation notPaidReservation : notPaidReservations) {
            Seat seat = concertRepository.findSeatByIdWithPessimisticLock(notPaidReservation.getSeatId());

            seat.toAvailable();
            concertRepository.saveSeat(seat);

            notPaidReservation.changeExpiredStatus();
            reservationRepository.save(notPaidReservation);
        }
    }

}
