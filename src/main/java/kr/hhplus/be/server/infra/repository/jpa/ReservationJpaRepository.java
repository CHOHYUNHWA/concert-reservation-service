package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Reservation;
import kr.hhplus.be.server.support.type.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUserId(Long userId);

    List<Reservation> findByStatusAndReservedAtBefore(ReservationStatus reservationStatus, LocalDateTime localDateTime);
}
