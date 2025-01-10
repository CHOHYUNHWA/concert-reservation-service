package kr.hhplus.be.server.infra.repository.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Seat> findById(Long seatId);

    List<Seat> findAllByConcertScheduleIdAndSeatStatus(Long concertScheduleId, SeatStatus seatStatus);
}
