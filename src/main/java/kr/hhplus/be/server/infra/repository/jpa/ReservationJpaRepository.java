package kr.hhplus.be.server.infra.repository.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.support.type.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = ?1")
    Optional<Reservation> findByIdWithPessimisticLock(Long id);

    Optional<Reservation> findByUserId(Long userId);

    List<Reservation> findByStatusAndReservedAtBefore(ReservationStatus reservationStatus, LocalDateTime localDateTime);

    List<Reservation> findByConcertIdAndConcertScheduleIdAndSeatId(long concertId, long concertScheduleId, long seatId);

    @Query("select r from Reservation r where r.id = ?1")
    Optional<Reservation> findByIdWithoutLock(Long reservationId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select r from Reservation r where r.id = ?1")
    Optional<Reservation> findByIdWithOptimisticLock(Long reservationId);
}
