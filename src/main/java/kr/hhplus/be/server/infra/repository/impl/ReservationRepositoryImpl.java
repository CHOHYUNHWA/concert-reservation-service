package kr.hhplus.be.server.infra.repository.impl;

import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.domain.repository.ReservationRepository;
import kr.hhplus.be.server.infra.repository.jpa.ReservationJpaRepository;
import kr.hhplus.be.server.support.exception.CustomException;
import kr.hhplus.be.server.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Reservation findByUserId(Long userId) {
        return reservationJpaRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    @Override
    public List<Reservation> findByConcertIdAndConcertScheduleIdAndSeatId(long concertId, long concertScheduleId, long seatId) {
        return reservationJpaRepository.findByConcertIdAndConcertScheduleIdAndSeatId(concertId, concertScheduleId, seatId);
    }
}
