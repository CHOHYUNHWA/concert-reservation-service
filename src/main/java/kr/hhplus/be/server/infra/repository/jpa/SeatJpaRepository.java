package kr.hhplus.be.server.infra.repository.jpa;

import kr.hhplus.be.server.domain.entity.Seat;
import kr.hhplus.be.server.support.type.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByConcertScheduleIdAndSeatStatus(Long concertScheduleId, SeatStatus seatStatus);
}
