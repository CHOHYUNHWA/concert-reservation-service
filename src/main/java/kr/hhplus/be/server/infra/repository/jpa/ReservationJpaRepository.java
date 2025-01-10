package kr.hhplus.be.server.infra.repository.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.support.type.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findById(Long id);

    Optional<Reservation> findByUserId(Long userId);

    List<Reservation> findByStatusAndReservedAtBefore(ReservationStatus reservationStatus, LocalDateTime localDateTime);

    List<Reservation> findByConcertIdAndConcertScheduleIdAndSeatId(long concertId, long concertScheduleId, long seatId);
}
