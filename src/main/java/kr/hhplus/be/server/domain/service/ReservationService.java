package kr.hhplus.be.server.domain.service;

import kr.hhplus.be.server.domain.entity.ConcertSchedule;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    //예약 생성
    public Reservation createReservation(ConcertSchedule concertSchedule, Seat seat, Long userId){
        Reservation reservation = Reservation.create(concertSchedule, seat.getId(), userId);
        return reservationRepository.save(reservation);
    }

    //예약 정보확인
    public Reservation getReservationByUserId(Long userId){
        return reservationRepository.findByUserId(userId);
    }

    //예약 상태 검증
    public Reservation validateReservation(Long reservationId, Long userId){
        Reservation reservation = reservationRepository.findByIdWithLock(reservationId);
        reservation.validateReservation(userId);

        return reservation;
    }

    //예약 완료 변경
    public Reservation changeCompletedStatus(Reservation reservation){
        reservation.changeCompletedStatus();
        return reservationRepository.save(reservation);
    }

    public Reservation validateReservationWithoutLock(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findByIdWithoutLock(reservationId);
        reservation.validateReservation(userId);

        return reservation;
    }
}
