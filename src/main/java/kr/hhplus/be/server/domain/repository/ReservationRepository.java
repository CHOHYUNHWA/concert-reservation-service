package kr.hhplus.be.server.domain.repository;

import kr.hhplus.be.server.domain.entity.Reservation;

import java.util.List;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Reservation findByUserId(Long userId);

    Reservation findByIdWithPessimisticLock(Long reservationId);

    Reservation findById(Long reservationId);

    List<Reservation> findByConcertIdAndConcertScheduleIdAndSeatId(long concertId, long concertScheduleId, long seatId);

    Reservation findByIdWithoutLock(Long reservationId);

    Reservation findByIdWithOptimisticLock(Long reservationId);
}
